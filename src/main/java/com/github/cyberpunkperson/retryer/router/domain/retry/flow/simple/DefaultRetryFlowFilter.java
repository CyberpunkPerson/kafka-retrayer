package com.github.cyberpunkperson.retryer.router.domain.retry.flow.simple;

import com.github.cyberpunkperson.retryer.router.domain.retry.flow.FlowManager;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.core.GenericSelector;
import org.springframework.stereotype.Component;
import src.main.java.com.github.cyberpunkperson.retryer.router.Retryer.RetryEntry;

import static src.main.java.com.github.cyberpunkperson.retryer.router.Retryer.RetryEntry.Flow.DEFAULT;

@RequiredArgsConstructor
@Component("defaultRetryFilter")
class DefaultRetryFlowFilter implements GenericSelector<RetryEntry> {

    private final FlowManager flowManager;

    @Override
    public boolean accept(RetryEntry entry) {
        return entry.getFlow().equals(DEFAULT) && !flowManager.isFlowOver(entry);
    }
}
