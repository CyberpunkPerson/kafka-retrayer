package com.github.cyberpunkperson.retryer.router.domain.router.queue.configuration.registry;

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
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerRouter.RouterQueueRecord;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerRouter.RouterQueueRecord.RetryDelay;

import java.time.Duration;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.springframework.integration.dsl.IntegrationFlows.from;

@Service
@RequiredArgsConstructor
class RouterQueueRecordSourceFactory<K, V> {

    private final MessageChannel routerQueueChannel;
    private final MessagePublishingErrorHandler routerQueueMessagePublishingErrorHandler;
    private final ConsumerFactory<K, V> routerConsumerFactory;

    private static final String FLOW_SUFFIX = "RetryFlow";
    private static final String QUEUE_RECORD_SOURCE_SUFFIX = "QueueRecordSource";


    public RouterFlowBundle<K, V> createMessageSourceFlow(RetryDelay retryDelay) {
        var source = buildMessageSource(retryDelay);
        source.setBeanName(formatBeanName(retryDelay, QUEUE_RECORD_SOURCE_SUFFIX));
        var flow = from(source, specification ->
                specification
                        .id(source.getBeanName())
                        .poller(Pollers
                                .fixedRate(Duration.ofSeconds(10))
                                .taskExecutor(buildTaskExecutor(formatBeanName(retryDelay, QUEUE_RECORD_SOURCE_SUFFIX + "-%d")))
                                .errorHandler(routerQueueMessagePublishingErrorHandler))
        )
                .channel(routerQueueChannel)
                .get();
        flow.setBeanName(formatBeanName(retryDelay, FLOW_SUFFIX));
        return new RouterFlowBundle<>(retryDelay, source, flow);
    }

    private KafkaMessageSource<K, V> buildMessageSource(RetryDelay retryDelay) {
        var consumerProperties = new ConsumerProperties(retryDelay.getTopic());
        return Kafka
                .inboundChannelAdapter(
                        routerConsumerFactory,
                        consumerProperties,
                        buildAckCallbackFactory(consumerProperties)
                )
                .messageConverter(new ProtoMessageConverter<>(RouterQueueRecord.parser()))
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
