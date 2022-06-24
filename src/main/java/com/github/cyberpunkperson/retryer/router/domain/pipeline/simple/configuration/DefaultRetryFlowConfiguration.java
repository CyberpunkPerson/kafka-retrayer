package com.github.cyberpunkperson.retryer.router.domain.pipeline.simple.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.core.GenericSelector;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.kafka.dsl.KafkaProducerMessageHandlerSpec;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.messaging.MessageChannel;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerQueue.QueueRecord;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerEntry.RetryRecord;

import java.util.function.Function;

import static com.github.cyberpunkperson.retryer.router.support.header.InternalHeader.*;
import static org.springframework.integration.dsl.IntegrationFlows.from;

@Configuration(proxyBeanMethods = false)
class DefaultRetryFlowConfiguration {

    @Bean
    IntegrationFlow defaultRetryFlow(MessageChannel entryChannel,
                                     GenericSelector<RetryRecord> defaultRetryFilter,
                                     GenericTransformer<RetryRecord, QueueRecord> defaultRetryFlowTransformer,
                                     KafkaProducerMessageHandlerSpec<byte[], byte[], ?>exitChannelAdapter) {
        return from(entryChannel)
                .filter(defaultRetryFilter)
                .transform(defaultRetryFlowTransformer)
                .enrichHeaders(extract(RECORD_KEY, (Function<QueueRecord, byte[]>) record -> record.getApplicationName().getBytes()))
                .enrichHeaders(extract(RECORD_TOPIC, (Function<QueueRecord, String>) record -> record.getDelay().getTopic()))
                .transform(QueueRecord::toByteArray)
                .handle(exitChannelAdapter)
                .get();
    }
}
