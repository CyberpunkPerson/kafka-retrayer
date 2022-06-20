package com.github.cyberpunkperson.retryer.router.domain.router.queue.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.integration.kafka.dsl.KafkaProducerMessageHandlerSpec;
import org.springframework.messaging.MessageChannel;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerRouter.RouterQueueRecord;

import static com.github.cyberpunkperson.retryer.router.support.header.InternalHeader.*;
import static org.springframework.integration.dsl.IntegrationFlows.from;
import static org.springframework.integration.dsl.MessageChannels.queue;

@Configuration(proxyBeanMethods = false)
class RouterQueueConfiguration {

    @Bean
    MessageChannel routerQueueChannel() {
        return queue().get();
    }

    @Bean
    IntegrationFlow routerQueueFlow(MessageChannel routerQueueChannel,
                                    GenericHandler<RouterQueueRecord> routerQueueRecordBarrier,
                                    KafkaProducerMessageHandlerSpec<byte[], byte[], ?> outboundChannelAdapter) {
        return from(routerQueueChannel)
                .handle(routerQueueRecordBarrier)
                .enrichHeaders(extract(RECORD_KEY, RouterQueueRecord::getKey))
                .enrichHeaders(extract(RECORD_TOPIC, RouterQueueRecord::getTopic))
                .<RouterQueueRecord, byte[]>transform(record -> record.getValue().toByteArray())
                .handle(outboundChannelAdapter)
                .get();
    }
}
