package com.github.cyberpunkperson.retrayer.domain.retry.flow.simple.configuration;

import com.github.cyberpunkperson.retrayer.domain.retry.flow.RetryEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.core.GenericSelector;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.messaging.MessageChannel;

import static org.springframework.integration.dsl.IntegrationFlows.from;

@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
class DefaultRetryFlowConfiguration {

    @Bean
    IntegrationFlow defaultRetryFlow(MessageChannel inboundSourceChannel,
                                     GenericSelector<RetryEntry> defaultRetryFilter,
                                     GenericHandler<RetryEntry> defaultRetryFlowHandler,
                                     MessageChannel outboundRetryChannel) {
        return from(inboundSourceChannel)
                .filter(defaultRetryFilter)
                .handle(defaultRetryFlowHandler) //todo replace on transformer
                .channel(outboundRetryChannel)
                .get();
    }
}
