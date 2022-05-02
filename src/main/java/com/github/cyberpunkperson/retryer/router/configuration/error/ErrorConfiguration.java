package com.github.cyberpunkperson.retryer.router.configuration.error;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import static org.springframework.integration.dsl.IntegrationFlows.from;
import static org.springframework.integration.dsl.MessageChannels.direct;

@Configuration(proxyBeanMethods = false)
class ErrorConfiguration {

    @Bean
    MessageChannel errorChannel() {
        return direct().get();
    }

    @Bean
    IntegrationFlow errorFlow(MessageChannel errorChannel,
                              MessageHandler errorHandler) {
        return from(errorChannel)
                .handle(errorHandler)
                .get();
    }
}
