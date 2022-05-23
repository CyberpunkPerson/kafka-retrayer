package com.github.cyberpunkperson.retryer.router.domain.retry.flow;

import com.github.cyberpunkperson.retryer.router.domain.retry.configuration.properties.RetryProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerRouter.RetryerQueueRecord.RetryInterval;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerSource.RetryRecord;

@Service
@RequiredArgsConstructor
public class FlowManager {

    private final RetryProperties retryProperties;


    public boolean isFlowOver(RetryRecord retryRecord) {
        var flow = retryProperties.getFlow(retryRecord.getFlow());
        return retryRecord.getDeliveryAttempt() > flow.size() - 1;
    }

    public RetryInterval getNextInterval(RetryRecord retryRecord) {
        return retryProperties.getInterval(retryRecord.getFlow(), retryRecord.getDeliveryAttempt() + 1);
    }
}
