package com.github.cyberpunkperson.retryer.router.domain.retry.flow.simple;

import com.github.cyberpunkperson.retryer.router.domain.retry.flow.FlowManager;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.core.GenericSelector;
import org.springframework.stereotype.Component;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerSource.RetryRecord;

import static src.main.java.com.github.cyberpunkperson.retryer.router.RetryerSource.RetryRecord.Flow.DEFAULT;

@RequiredArgsConstructor
@Component("defaultRetryFilter")
class DefaultRetryFlowFilter implements GenericSelector<RetryRecord> {

    private final FlowManager flowManager;

    @Override
    public boolean accept(RetryRecord retryRecord) {
        return retryRecord.getFlow().equals(DEFAULT) && !flowManager.isFlowOver(retryRecord);
    }
}
