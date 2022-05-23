package com.github.cyberpunkperson.retryer.router.domain.archive;

import com.github.cyberpunkperson.retryer.router.domain.retry.flow.FlowManager;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.core.GenericSelector;
import org.springframework.stereotype.Component;
import src.main.java.com.github.cyberpunkperson.retryer.router.Retryer.RetryEntry;

@RequiredArgsConstructor
@Component("archiveFlowFilter")
class ArchiveFlowFilter implements GenericSelector<RetryEntry> {

    private final FlowManager flowManager;

    @Override
    public boolean accept(RetryEntry retryEntry) {
        return flowManager.isFlowOver(retryEntry);
    }
}
