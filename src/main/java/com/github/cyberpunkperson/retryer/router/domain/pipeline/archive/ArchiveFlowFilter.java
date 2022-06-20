package com.github.cyberpunkperson.retryer.router.domain.pipeline.archive;

import com.github.cyberpunkperson.retryer.router.domain.pipeline.PipelineManager;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.core.GenericSelector;
import org.springframework.stereotype.Component;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerEntry.RetryRecord;

@RequiredArgsConstructor
@Component("archiveFlowFilter")
class ArchiveFlowFilter implements GenericSelector<RetryRecord> {

    private final PipelineManager pipelineManager;

    @Override
    public boolean accept(RetryRecord retryRecord) {
        return pipelineManager.isBackoffOver(retryRecord);
    }
}
