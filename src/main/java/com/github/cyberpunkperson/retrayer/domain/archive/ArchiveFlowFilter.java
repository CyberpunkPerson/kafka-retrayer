package com.github.cyberpunkperson.retrayer.domain.archive;

import com.github.cyberpunkperson.retrayer.domain.retry.configuration.properties.RetryProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.core.MessageSelector;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import static com.github.cyberpunkperson.retrayer.domain.archive.configuration.ArchiveFlowConfiguration.FLOW_SPENT;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.IntegrationHeaders.getRecordFlowContext;

@RequiredArgsConstructor
@Component("archiveFlowFilter")
class ArchiveFlowFilter implements MessageSelector {

    private final RetryProperties retryProperties;


    @Override
    public boolean accept(Message<?> message) {
        var context = getRecordFlowContext(message.getHeaders());
        return FLOW_SPENT.test(context, retryProperties.getFlow(context.retryFlow()));
    }
}
