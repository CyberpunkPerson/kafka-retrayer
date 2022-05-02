package com.github.cyberpunkperson.retryer.router.domain.retry.loop.configuration;

import com.google.protobuf.AbstractMessageLite;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.integration.kafka.dsl.KafkaProducerMessageHandlerSpec;
import org.springframework.messaging.MessageChannel;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerRouter.LoopEntry;

import static com.github.cyberpunkperson.retryer.router.integration.metadata.headers.IntegrationHeaders.ENTRY_KEY;
import static com.github.cyberpunkperson.retryer.router.integration.metadata.headers.IntegrationHeaders.ENTRY_TOPIC;
import static org.springframework.integration.dsl.IntegrationFlows.from;
import static org.springframework.integration.dsl.MessageChannels.direct;

@Configuration(proxyBeanMethods = false)
class RetryLoopConfiguration {

    @Bean
    MessageChannel retryLoopChannel() {
        return direct().get();
    }

    @Bean
    IntegrationFlow retryLoopFlow(MessageChannel retryLoopChannel,
                                  GenericHandler<LoopEntry> retryLoopBarrier,
                                  KafkaProducerMessageHandlerSpec<byte[], byte[], ?> outboundRetryChannelAdapter) {
        return from(retryLoopChannel)
                .handle(retryLoopBarrier)
                .enrichHeaders(enricher ->
                        enricher.<LoopEntry>headerFunction(ENTRY_KEY, message -> message.getPayload().getKey()))
                .enrichHeaders(enricher ->
                        enricher.<LoopEntry>headerFunction(ENTRY_TOPIC, message -> message.getPayload().getTopic()))
                .<LoopEntry, byte[]>transform(AbstractMessageLite::toByteArray) //todo refactor to take record value
                .handle(outboundRetryChannelAdapter)
                .get();
    }
}
