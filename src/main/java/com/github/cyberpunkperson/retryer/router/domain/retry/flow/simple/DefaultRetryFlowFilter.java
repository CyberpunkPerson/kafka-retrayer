package com.github.cyberpunkperson.retryer.router.domain.retry.flow.simple;

import com.github.cyberpunkperson.retryer.router.domain.retry.configuration.properties.RetryProperties;
import com.github.cyberpunkperson.retryer.router.domain.retry.flow.RetryEntry;
import com.github.cyberpunkperson.retryer.router.domain.archive.configuration.ArchiveFlowConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.core.GenericSelector;
import org.springframework.stereotype.Component;

import static com.github.cyberpunkperson.retryer.router.integration.metadata.headers.IntegrationHeaders.DEFAULT_FLOW;

@RequiredArgsConstructor
@Component("defaultRetryFilter")
class DefaultRetryFlowFilter implements GenericSelector<RetryEntry> {

    private final RetryProperties retryProperties;


    @Override //todo make context know about it's spent instead of static predicate
    public boolean accept(RetryEntry context) {
        return DEFAULT_FLOW.equals(context.retryFlow()) &&
                ArchiveFlowConfiguration.FLOW_SPENT.negate().test(context, retryProperties.getFlow(context.retryFlow()));
    }
}
