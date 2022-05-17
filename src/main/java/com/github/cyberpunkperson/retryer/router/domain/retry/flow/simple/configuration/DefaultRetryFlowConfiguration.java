package com.github.cyberpunkperson.retryer.router.domain.retry.flow.simple.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.core.GenericSelector;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.kafka.dsl.KafkaProducerMessageHandlerSpec;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.messaging.MessageChannel;
import src.main.java.com.github.cyberpunkperson.retryer.router.Retryer.RetryEntry;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerRouter.LoopEntry;

import java.util.function.Function;

import static com.github.cyberpunkperson.retryer.router.integration.metadata.headers.IntegrationHeaders.*;
import static org.springframework.integration.dsl.IntegrationFlows.from;

@Configuration(proxyBeanMethods = false)
class DefaultRetryFlowConfiguration {

    @Bean
    IntegrationFlow defaultRetryFlow(MessageChannel inboundSourceChannel,
                                     GenericSelector<RetryEntry> defaultRetryFilter,
                                     GenericTransformer<RetryEntry, LoopEntry> defaultRetryFlowTransformer,
                                     KafkaProducerMessageHandlerSpec<byte[], byte[], ?> outboundRetryChannelAdapter) {
        return from(inboundSourceChannel)
                .filter(defaultRetryFilter)
                .transform(defaultRetryFlowTransformer)
                .enrichHeaders(extract(ENTRY_KEY, (Function<LoopEntry, byte[]>) entry -> entry.getApplicationName().getBytes()))
                .enrichHeaders(extract(ENTRY_TOPIC, (Function<LoopEntry, String>) entry -> entry.getInterval().getTopic()))
                .transform(LoopEntry::toByteArray)
                .handle(outboundRetryChannelAdapter)
                .get();
    }
}
