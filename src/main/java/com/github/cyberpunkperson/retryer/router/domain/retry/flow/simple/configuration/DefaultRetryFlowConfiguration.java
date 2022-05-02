package com.github.cyberpunkperson.retryer.router.domain.retry.flow.simple.configuration;

import com.google.protobuf.AbstractMessageLite;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.core.GenericSelector;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.kafka.dsl.KafkaProducerMessageHandlerSpec;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.messaging.MessageChannel;
import src.main.java.com.github.cyberpunkperson.retryer.router.Retryer.RetryEntry;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerRouter.LoopEntry;

import static com.github.cyberpunkperson.retryer.router.integration.metadata.headers.IntegrationHeaders.ENTRY_KEY;
import static com.github.cyberpunkperson.retryer.router.integration.metadata.headers.IntegrationHeaders.ENTRY_TOPIC;
import static org.springframework.integration.dsl.IntegrationFlows.from;

@RequiredArgsConstructor
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
                .enrichHeaders(enricher ->
                        enricher.<LoopEntry>headerFunction(ENTRY_KEY, message -> message.getPayload().getApplicationName().getBytes()))
                .enrichHeaders(enricher ->
                        enricher.<LoopEntry>headerFunction(ENTRY_TOPIC, message -> message.getPayload().getInterval().getTopic()))
                .<LoopEntry, byte[]>transform(AbstractMessageLite::toByteArray) //todo refactor
                .handle(outboundRetryChannelAdapter)
                .get();
    }
}
