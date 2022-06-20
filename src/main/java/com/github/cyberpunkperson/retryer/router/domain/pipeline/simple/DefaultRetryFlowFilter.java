package com.github.cyberpunkperson.retryer.router.domain.pipeline.simple;

import com.github.cyberpunkperson.retryer.router.domain.pipeline.PipelineManager;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.core.GenericSelector;
import org.springframework.stereotype.Component;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerEntry.RetryRecord;

import static src.main.java.com.github.cyberpunkperson.retryer.router.RetryerEntry.RetryRecord.Backoff.DEFAULT;

@RequiredArgsConstructor
@Component("defaultRetryFilter")
class DefaultRetryFlowFilter implements GenericSelector<RetryRecord> {

    private final PipelineManager pipelineManager;

    @Override
    public boolean accept(RetryRecord retryRecord) {
        return retryRecord.getBackoff().equals(DEFAULT) && !pipelineManager.isBackoffOver(retryRecord);
    }
}
