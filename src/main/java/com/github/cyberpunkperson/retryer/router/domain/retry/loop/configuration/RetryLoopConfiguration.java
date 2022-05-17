package com.github.cyberpunkperson.retryer.router.domain.retry.loop.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.integration.kafka.dsl.KafkaProducerMessageHandlerSpec;
import org.springframework.messaging.MessageChannel;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerRouter.LoopEntry;

import static com.github.cyberpunkperson.retryer.router.integration.metadata.headers.IntegrationHeaders.*;
import static org.springframework.integration.dsl.IntegrationFlows.from;
import static org.springframework.integration.dsl.MessageChannels.queue;

@Configuration(proxyBeanMethods = false)
class RetryLoopConfiguration {

    @Bean
    MessageChannel retryLoopChannel() {
        return queue().get();
    }

    @Bean
    IntegrationFlow retryLoopFlow(MessageChannel retryLoopChannel,
                                  GenericHandler<LoopEntry> retryLoopBarrier,
                                  KafkaProducerMessageHandlerSpec<byte[], byte[], ?> outboundRetryChannelAdapter) {
        return from(retryLoopChannel)
                .handle(retryLoopBarrier)
                .enrichHeaders(extract(ENTRY_KEY, LoopEntry::getKey))
                .enrichHeaders(extract(ENTRY_TOPIC, LoopEntry::getTopic))
                .<LoopEntry, byte[]>transform(entry -> entry.getValue().toByteArray())
                .handle(outboundRetryChannelAdapter)
                .get();
    }
}
