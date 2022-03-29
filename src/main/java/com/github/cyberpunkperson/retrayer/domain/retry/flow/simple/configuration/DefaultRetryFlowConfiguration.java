package com.github.cyberpunkperson.retrayer.domain.retry.flow.simple.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.core.MessageSelector;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import static org.springframework.integration.dsl.IntegrationFlows.from;

@Configuration(proxyBeanMethods = false)
class DefaultRetryFlowConfiguration {


    @Bean
    IntegrationFlow defaultRetryFlow(MessageChannel inboundSourceChannel,
                                     MessageSelector defaultRetryFilter,
                                     MessageHandler defaultRetryHandler) {
        return from(inboundSourceChannel)
                .filter(defaultRetryFilter)
                .handle(defaultRetryHandler)
                .get();
    }
}
