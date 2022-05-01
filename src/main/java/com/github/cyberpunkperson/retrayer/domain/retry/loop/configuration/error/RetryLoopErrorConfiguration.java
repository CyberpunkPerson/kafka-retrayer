package com.github.cyberpunkperson.retrayer.domain.retry.loop.configuration.error;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import static org.springframework.integration.dsl.IntegrationFlows.from;
import static org.springframework.integration.dsl.MessageChannels.direct;

@Configuration(proxyBeanMethods = false)
class RetryLoopErrorConfiguration {


    @Bean
    MessageChannel retryLoopErrorChannel() {
        return direct().get();
    }

    @Bean
    IntegrationFlow retryLoopErrorFlow(MessageChannel retryLoopErrorChannel,
                                       MessageHandler retryLoopErrorHandler) {
        return from(retryLoopErrorChannel)
                .handle(retryLoopErrorHandler)
                .get();
    }
}
