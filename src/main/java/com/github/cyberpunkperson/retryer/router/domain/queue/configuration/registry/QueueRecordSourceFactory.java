package com.github.cyberpunkperson.retryer.router.domain.queue.configuration.registry;

import com.github.cyberpunkperson.retryer.router.configuration.converter.ProtoMessageConverter;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.acks.AcknowledgmentCallback;
import org.springframework.integration.channel.MessagePublishingErrorHandler;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.integration.kafka.inbound.KafkaMessageSource;
import org.springframework.integration.kafka.inbound.KafkaMessageSource.KafkaAckCallback;
import org.springframework.integration.kafka.inbound.KafkaMessageSource.KafkaAckCallbackFactory;
import org.springframework.integration.kafka.inbound.KafkaMessageSource.KafkaAckInfo;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ConsumerProperties;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerQueue.QueueRecord;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerQueue.QueueRecord.RetryDelay;

import java.time.Duration;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.springframework.integration.dsl.IntegrationFlows.from;

@Service
@RequiredArgsConstructor
class QueueRecordSourceFactory<K, V> {

    private final MessageChannel queueChannel;
    private final MessagePublishingErrorHandler queueMessagePublishingErrorHandler;
    private final ConsumerFactory<K, V> queueConsumerFactory;

    private static final String FLOW_SUFFIX = "QueueFlow";
    private static final String QUEUE_RECORD_SOURCE_SUFFIX = "QueueRecordSource";


    public QueueBundle<K, V> createMessageSourceFlow(RetryDelay retryDelay) {
        var source = buildMessageSource(retryDelay);
        source.setBeanName(formatBeanName(retryDelay, QUEUE_RECORD_SOURCE_SUFFIX));
        var flow = from(source, specification ->
                specification
                        .id(source.getBeanName())
                        .poller(Pollers
                                .fixedRate(Duration.ofSeconds(10))
                                .taskExecutor(buildTaskExecutor(formatBeanName(retryDelay, QUEUE_RECORD_SOURCE_SUFFIX + "-%d")))
                                .errorHandler(queueMessagePublishingErrorHandler))
        )
                .channel(queueChannel)
                .get();
        flow.setBeanName(formatBeanName(retryDelay, FLOW_SUFFIX));
        return new QueueBundle<>(retryDelay, source, flow);
    }

    private KafkaMessageSource<K, V> buildMessageSource(RetryDelay retryDelay) {
        var consumerProperties = new ConsumerProperties(retryDelay.getTopic());
        return Kafka
                .inboundChannelAdapter(
                        queueConsumerFactory,
                        consumerProperties,
                        buildAckCallbackFactory(consumerProperties)
                )
                .messageConverter(new ProtoMessageConverter<>(QueueRecord.parser()))
                .get();
    }

    private static String formatBeanName(RetryDelay retryDelay, String suffix) {
        return Duration.ofSeconds(retryDelay.getDuration().getSeconds()).toString().toLowerCase() + suffix;
    }

    private static ExecutorService buildTaskExecutor(String threadNameFormat) {
        var threadFactory = new ThreadFactoryBuilder()
                .setNameFormat(threadNameFormat)
                .build();
        return newSingleThreadExecutor(threadFactory);
    }

    private KafkaAckCallbackFactory<K, V> buildAckCallbackFactory(ConsumerProperties consumerProperties) {
        return new KafkaAckCallbackFactory<>(consumerProperties) {
            @Override
            public AcknowledgmentCallback createCallback(KafkaAckInfo<K, V> info) {
                var callback = new KafkaAckCallback<>(info, consumerProperties);
                callback.noAutoAck();
                return callback;
            }
        };
    }
}
