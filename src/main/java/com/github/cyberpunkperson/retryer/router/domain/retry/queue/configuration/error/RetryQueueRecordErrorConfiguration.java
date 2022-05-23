package com.github.cyberpunkperson.retryer.router.domain.retry.queue.configuration.error;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.MessagePublishingErrorHandler;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import static org.springframework.integration.dsl.IntegrationFlows.from;
import static org.springframework.integration.dsl.MessageChannels.direct;

@Configuration(proxyBeanMethods = false)
class RetryQueueRecordErrorConfiguration {


    @Bean
    MessageChannel retryerQueueErrorChannel() {
        return direct().get();
    }

    @Bean
    IntegrationFlow retryerQueueErrorFlow(MessageChannel retryerQueueErrorChannel,
                                          MessageHandler retryQueueRecordErrorHandler) {
        return from(retryerQueueErrorChannel)
                .handle(retryQueueRecordErrorHandler)
                .get();
    }

    @Bean
    MessagePublishingErrorHandler retryerQueueMessagePublishingErrorHandler(MessageChannel retryerQueueErrorChannel) {
        var errorHandler = new MessagePublishingErrorHandler();
        errorHandler.setDefaultErrorChannel(retryerQueueErrorChannel);
        return errorHandler;
    }
}
