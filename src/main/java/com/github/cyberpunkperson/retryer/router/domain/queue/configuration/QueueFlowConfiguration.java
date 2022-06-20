package com.github.cyberpunkperson.retryer.router.domain.queue.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.integration.kafka.dsl.KafkaProducerMessageHandlerSpec;
import org.springframework.messaging.MessageChannel;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerQueue.QueueRecord;

import static com.github.cyberpunkperson.retryer.router.support.header.InternalHeader.*;
import static org.springframework.integration.dsl.IntegrationFlows.from;
import static org.springframework.integration.dsl.MessageChannels.queue;

@Configuration(proxyBeanMethods = false)
class QueueFlowConfiguration {

    @Bean
    MessageChannel queueChannel() {
        return queue().get();
    }

    @Bean
    IntegrationFlow queueFlow(MessageChannel queueChannel,
                              GenericHandler<QueueRecord> queueRecordBarrier,
                              KafkaProducerMessageHandlerSpec<byte[], byte[], ?>exitChannelAdapter) {
        return from(queueChannel)
                .handle(queueRecordBarrier)
                .enrichHeaders(extract(RECORD_KEY, QueueRecord::getKey))
                .enrichHeaders(extract(RECORD_TOPIC, QueueRecord::getTopic))
                .<QueueRecord, byte[]>transform(record -> record.getValue().toByteArray())
                .handle(exitChannelAdapter)
                .get();
    }
}
