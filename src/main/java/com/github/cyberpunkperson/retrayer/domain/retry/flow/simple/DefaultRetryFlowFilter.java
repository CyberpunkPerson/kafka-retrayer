package com.github.cyberpunkperson.retrayer.domain.retry.flow.simple;

import com.github.cyberpunkperson.retrayer.domain.retry.configuration.properties.RetryProperties;
import com.github.cyberpunkperson.retrayer.domain.retry.flow.RetryEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.core.GenericSelector;
import org.springframework.stereotype.Component;

import static com.github.cyberpunkperson.retrayer.domain.archive.configuration.ArchiveFlowConfiguration.FLOW_SPENT;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.IntegrationHeaders.DEFAULT_FLOW;

@RequiredArgsConstructor
@Component("defaultRetryFilter")
class DefaultRetryFlowFilter implements GenericSelector<RetryEntry> {

    private final RetryProperties retryProperties;


    @Override //todo make context know about it's spent instead of static predicate
    public boolean accept(RetryEntry context) {
        return DEFAULT_FLOW.equals(context.retryFlow()) &&
                FLOW_SPENT.negate().test(context, retryProperties.getFlow(context.retryFlow()));
    }
}
