package com.github.cyberpunkperson.retryer.router.domain.archive.configuration;

import com.github.cyberpunkperson.retryer.router.domain.retry.configuration.properties.RetryProperties.RetryInterval;
import com.github.cyberpunkperson.retryer.router.domain.retry.flow.RetryEntry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.core.GenericSelector;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageChannel;

import java.util.List;
import java.util.function.BiPredicate;

import static org.springframework.integration.dsl.IntegrationFlows.from;

@Configuration(proxyBeanMethods = false)
public class ArchiveFlowConfiguration {

    public static final BiPredicate<RetryEntry, List<RetryInterval>> FLOW_SPENT = (flowContext, flowDelays) ->
            flowContext.deliveryAttempt() < 0 || flowContext.deliveryAttempt() >= flowDelays.size();

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
