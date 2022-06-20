package com.github.cyberpunkperson.retryer.router.domain.queue.error;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.MessagePublishingErrorHandler;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import static org.springframework.integration.dsl.IntegrationFlows.from;
import static org.springframework.integration.dsl.MessageChannels.direct;

@Configuration(proxyBeanMethods = false)
class QueueErrorConfiguration {


    @Bean
    MessageChannel queueErrorChannel() {
        return direct().get();
    }

    @Bean
    IntegrationFlow queueErrorFlow(MessageChannel queueErrorChannel,
                                         MessageHandler queueRecordErrorHandler) {
        return from(queueErrorChannel)
                .handle(queueRecordErrorHandler)
                .get();
    }

    @Bean
    MessagePublishingErrorHandler queueMessagePublishingErrorHandler(MessageChannel queueErrorChannel) {
        var errorHandler = new MessagePublishingErrorHandler();
        errorHandler.setDefaultErrorChannel(queueErrorChannel);
        return errorHandler;
    }
}
