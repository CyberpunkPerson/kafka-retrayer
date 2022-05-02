package com.github.cyberpunkperson.retryer.router.domain.archive;

import com.github.cyberpunkperson.retryer.router.domain.retry.configuration.properties.RetryProperties;
import com.github.cyberpunkperson.retryer.router.domain.retry.flow.RetryEntry;
import com.github.cyberpunkperson.retryer.router.domain.archive.configuration.ArchiveFlowConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.core.GenericSelector;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component("archiveFlowFilter")
class ArchiveFlowFilter implements GenericSelector<RetryEntry> {

    private final RetryProperties retryProperties;

    @Override
    public boolean accept(RetryEntry context) {
        return ArchiveFlowConfiguration.FLOW_SPENT.test(context, retryProperties.getFlow(context.retryFlow()));
    }
}
