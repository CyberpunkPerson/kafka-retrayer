package com.github.cyberpunkperson.retryer.router.domain.retry.loop.configuration;

import com.github.cyberpunkperson.retryer.router.domain.retry.loop.LoopEntry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.messaging.MessageChannel;

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
                                  GenericHandler<LoopEntry> retryLoopHandler,
                                  MessageChannel outboundRetryChannel) {
        return from(retryLoopChannel)
                .handle(retryLoopHandler)
                .channel(outboundRetryChannel)
                .get();
    }
}
