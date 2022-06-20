package com.github.cyberpunkperson.retryer.router.domain.router.queue.configuration.error;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.MessagePublishingErrorHandler;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import static org.springframework.integration.dsl.IntegrationFlows.from;
import static org.springframework.integration.dsl.MessageChannels.direct;

@Configuration(proxyBeanMethods = false)
class RouterQueueRecordErrorConfiguration {


    @Bean
    MessageChannel routerQueueErrorChannel() {
        return direct().get();
    }

    @Bean
    IntegrationFlow routerQueueErrorFlow(MessageChannel routerQueueErrorChannel,
                                         MessageHandler routerQueueRecordErrorHandler) {
        return from(routerQueueErrorChannel)
                .handle(routerQueueRecordErrorHandler)
                .get();
    }

    @Bean
    MessagePublishingErrorHandler routerQueueMessagePublishingErrorHandler(MessageChannel routerQueueErrorChannel) {
        var errorHandler = new MessagePublishingErrorHandler();
        errorHandler.setDefaultErrorChannel(routerQueueErrorChannel);
        return errorHandler;
    }
}
