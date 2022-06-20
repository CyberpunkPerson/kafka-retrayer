package com.github.cyberpunkperson.retryer.router.domain.pipeline;

import com.github.cyberpunkperson.retryer.router.domain.queue.configuration.properties.QueueProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerQueue.QueueRecord.RetryDelay;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerEntry.RetryRecord;

@Service
@RequiredArgsConstructor
public class PipelineManager {

    private final QueueProperties queueProperties;


    public boolean isBackoffOver(RetryRecord retryRecord) {
        var backoff = queueProperties.getBackoff(retryRecord.getBackoff());
        return retryRecord.getDeliveryAttempt() > backoff.size() - 1;
    }

    public RetryDelay getNextDelay(RetryRecord retryRecord) {
        return queueProperties.getDelay(retryRecord.getBackoff(), retryRecord.getDeliveryAttempt() + 1);
    }
}
