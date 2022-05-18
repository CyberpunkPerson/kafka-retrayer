package com.github.cyberpunkperson.retryer.router.domain.retry.flow;

import com.github.cyberpunkperson.retryer.router.domain.retry.configuration.properties.RetryProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import src.main.java.com.github.cyberpunkperson.retryer.router.Retryer.RetryEntry;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerRouter.LoopEntry.RetryInterval;

@Service
@RequiredArgsConstructor
public class FlowManager {

    private final RetryProperties retryProperties;


    public boolean isFlowOver(RetryEntry retryEntry) {
        var flow = retryProperties.getFlow(retryEntry.getFlow());
        return retryEntry.getDeliveryAttempt() > flow.size() - 1;
    }

    public RetryInterval getNextInterval(RetryEntry retryEntry) {
        return retryProperties.getInterval(retryEntry.getFlow(), retryEntry.getDeliveryAttempt() + 1);
    }
}
