package com.github.cyberpunkperson.retryer.router.domain.pipeline.simple;

import com.github.cyberpunkperson.retryer.router.domain.pipeline.PipelineManager;
import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.stereotype.Component;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerQueue.QueueRecord;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerQueue.QueueRecord.RetryDelay;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerEntry.RetryRecord;

import static com.google.protobuf.util.Timestamps.fromSeconds;
import static java.time.Instant.now;


@Component
@RequiredArgsConstructor
class DefaultRetryFlowTransformer implements GenericTransformer<RetryRecord, QueueRecord> {

    private final PipelineManager pipelineManager;
    private final RetryRecordMapper retryRecordMapper;

    @Override
    public QueueRecord transform(RetryRecord retryRecord) {
        var delay = pipelineManager.getNextDelay(retryRecord);
        return retryRecordMapper.newRetryerQueueRecordBuilder(retryRecord)
                .setDeliveryAttempt(retryRecord.getDeliveryAttempt() + 1)
                .setDelay(delay)
                .setRedeliveryTimestamp(getRedeliveryTimestamp(delay))
                .build();
    }

    private static Timestamp getRedeliveryTimestamp(RetryDelay delay) {
        return fromSeconds(now().plusSeconds(delay.getDuration().getSeconds()).getEpochSecond());
    }
}
