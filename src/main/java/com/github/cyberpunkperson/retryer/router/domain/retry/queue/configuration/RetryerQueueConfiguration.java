package com.github.cyberpunkperson.retryer.router.domain.retry.queue.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.integration.kafka.dsl.KafkaProducerMessageHandlerSpec;
import org.springframework.messaging.MessageChannel;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerRouter.RetryerQueueRecord;

import static com.github.cyberpunkperson.retryer.router.support.headers.InternalHeaders.*;
import static org.springframework.integration.dsl.IntegrationFlows.from;
import static org.springframework.integration.dsl.MessageChannels.queue;

@Configuration(proxyBeanMethods = false)
class RetryerQueueConfiguration {

    @Bean
    MessageChannel retryerQueueChannel() {
        return queue().get();
    }

    @Bean
    IntegrationFlow retryerQueueFlow(MessageChannel retryerQueueChannel,
                                     GenericHandler<RetryerQueueRecord> retryerQueueRecordBarrier,
                                     KafkaProducerMessageHandlerSpec<byte[], byte[], ?> outboundChannelAdapter) {
        return from(retryerQueueChannel)
                .handle(retryerQueueRecordBarrier)
                .enrichHeaders(extract(RECORD_KEY, RetryerQueueRecord::getKey))
                .enrichHeaders(extract(RECORD_TOPIC, RetryerQueueRecord::getTopic))
                .<RetryerQueueRecord, byte[]>transform(record -> record.getValue().toByteArray())
                .handle(outboundChannelAdapter)
                .get();
    }
}
