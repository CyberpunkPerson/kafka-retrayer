package com.github.cyberpunkperson.retrayer.domain.archive.configuration;

import com.github.cyberpunkperson.retrayer.domain.retry.flow.RecordFlowContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.core.MessageSelector;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageChannel;

import java.time.Duration;
import java.util.List;
import java.util.function.BiPredicate;

import static org.springframework.integration.dsl.IntegrationFlows.from;

@Configuration(proxyBeanMethods = false)
public class ArchiveFlowConfiguration {

    public static final BiPredicate<RecordFlowContext, List<Duration>> FLOW_SPENT = (flowContext, flowDelays) ->
            flowContext.deliveryAttempt() < 0 || flowContext.deliveryAttempt() >= flowDelays.size();

    @Bean
    IntegrationFlow archiveFlow(MessageChannel inboundSourceChannel,
                                MessageSelector archiveFlowFilter) {
        return from(inboundSourceChannel)
                .filter(archiveFlowFilter)
                .handle(message -> {
                    System.out.println("Hop stop!"); //todo impl
                })
                .get();
    }

}
