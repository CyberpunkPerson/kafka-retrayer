package com.github.cyberpunkperson.retryer.router.domain.archive;

import com.github.cyberpunkperson.retryer.router.domain.retry.flow.FlowManager;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.core.GenericSelector;
import org.springframework.stereotype.Component;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerSource.RetryRecord;

@RequiredArgsConstructor
@Component("archiveFlowFilter")
class ArchiveFlowFilter implements GenericSelector<RetryRecord> {

    private final FlowManager flowManager;

    @Override
    public boolean accept(RetryRecord retryRecord) {
        return flowManager.isFlowOver(retryRecord);
    }
}
