package com.github.cyberpunkperson.retryer.router.domain.retry.flow.simple.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.core.GenericSelector;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.kafka.dsl.KafkaProducerMessageHandlerSpec;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.messaging.MessageChannel;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerRouter.RetryerQueueRecord;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerSource.RetryRecord;

import java.util.function.Function;

import static com.github.cyberpunkperson.retryer.router.support.headers.InternalHeaders.*;
import static org.springframework.integration.dsl.IntegrationFlows.from;

@Configuration(proxyBeanMethods = false)
class DefaultRetryFlowConfiguration {

    @Bean
    IntegrationFlow defaultRetryFlow(MessageChannel inboundSourceChannel,
                                     GenericSelector<RetryRecord> defaultRetryFilter,
                                     GenericTransformer<RetryRecord, RetryerQueueRecord> defaultRetryFlowTransformer,
                                     KafkaProducerMessageHandlerSpec<byte[], byte[], ?> outboundChannelAdapter) {
        return from(inboundSourceChannel)
                .filter(defaultRetryFilter)
                .transform(defaultRetryFlowTransformer)
                .enrichHeaders(extract(RECORD_KEY, (Function<RetryerQueueRecord, byte[]>) record -> record.getApplicationName().getBytes()))
                .enrichHeaders(extract(RECORD_TOPIC, (Function<RetryerQueueRecord, String>) record -> record.getInterval().getTopic()))
                .transform(RetryerQueueRecord::toByteArray)
                .handle(outboundChannelAdapter)
                .get();
    }
}
