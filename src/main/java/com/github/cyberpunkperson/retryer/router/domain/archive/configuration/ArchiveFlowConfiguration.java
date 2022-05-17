package com.github.cyberpunkperson.retryer.router.domain.archive.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.core.GenericSelector;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageChannel;
import src.main.java.com.github.cyberpunkperson.retryer.router.Retryer.RetryEntry;

import static org.springframework.integration.dsl.IntegrationFlows.from;

@Configuration(proxyBeanMethods = false)
public class ArchiveFlowConfiguration {

    @Bean
    IntegrationFlow archiveFlow(MessageChannel inboundSourceChannel,
                                GenericSelector<RetryEntry> archiveFlowFilter) {
        return from(inboundSourceChannel)
                .filter(archiveFlowFilter)
                .handle(message -> {
                    System.out.println("Hop stop!"); //todo impl
                })
                .get();
    }

}
