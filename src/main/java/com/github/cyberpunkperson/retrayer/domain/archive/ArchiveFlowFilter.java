package com.github.cyberpunkperson.retrayer.domain.archive;

import com.github.cyberpunkperson.retrayer.domain.retry.configuration.properties.RetryProperties;
import com.github.cyberpunkperson.retrayer.domain.retry.flow.RetryEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.core.GenericSelector;
import org.springframework.stereotype.Component;

import static com.github.cyberpunkperson.retrayer.domain.archive.configuration.ArchiveFlowConfiguration.FLOW_SPENT;

@RequiredArgsConstructor
@Component("archiveFlowFilter")
class ArchiveFlowFilter implements GenericSelector<RetryEntry> {

    private final RetryProperties retryProperties;

    @Override
    public boolean accept(RetryEntry context) {
        return FLOW_SPENT.test(context, retryProperties.getFlow(context.retryFlow()));
    }
}
