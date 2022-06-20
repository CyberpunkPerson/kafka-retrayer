package com.github.cyberpunkperson.retryer.router.domain.router.flow;

import com.github.cyberpunkperson.retryer.router.domain.router.queue.configuration.properties.RouterQueueProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerRouter.RouterQueueRecord.RetryDelay;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerSource.RetryRecord;

@Service
@RequiredArgsConstructor
public class FlowManager {

    private final RouterQueueProperties routerQueueProperties;


    public boolean isFlowOver(RetryRecord retryRecord) {
        var flow = routerQueueProperties.getFlow(retryRecord.getFlow());
        return retryRecord.getDeliveryAttempt() > flow.size() - 1;
    }

    public RetryDelay getNextInterval(RetryRecord retryRecord) {
        return routerQueueProperties.getDelay(retryRecord.getFlow(), retryRecord.getDeliveryAttempt() + 1);
    }
}
