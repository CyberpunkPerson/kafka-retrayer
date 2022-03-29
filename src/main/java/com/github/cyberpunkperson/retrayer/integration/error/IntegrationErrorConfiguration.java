package com.github.cyberpunkperson.retrayer.integration.error;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import static org.springframework.integration.dsl.IntegrationFlows.from;
import static org.springframework.integration.dsl.MessageChannels.direct;

@Configuration(proxyBeanMethods = false)
class IntegrationErrorConfiguration {

    @Bean
    MessageChannel integrationErrorChannel() {
        return direct().get();
    }

    @Bean
    IntegrationFlow integrationErrorFlow(MessageChannel integrationErrorChannel,
                                         MessageHandler integrationErrorHandler) {
        return from(integrationErrorChannel)
                .handle(integrationErrorHandler)
                .get();
    }
}
