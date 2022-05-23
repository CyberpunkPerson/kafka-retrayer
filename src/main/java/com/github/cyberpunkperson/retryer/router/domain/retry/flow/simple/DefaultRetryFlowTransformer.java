package com.github.cyberpunkperson.retryer.router.domain.retry.flow.simple;

import com.github.cyberpunkperson.retryer.router.domain.retry.flow.FlowManager;
import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.stereotype.Component;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerRouter.RetryerQueueRecord;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerRouter.RetryerQueueRecord.RetryInterval;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerSource.RetryRecord;

import static com.google.protobuf.util.Timestamps.fromSeconds;
import static java.time.Instant.now;


@Component
@RequiredArgsConstructor
class DefaultRetryFlowTransformer implements GenericTransformer<RetryRecord, RetryerQueueRecord> {

    private final FlowManager flowManager;
    private final RetryRecordMapper retryRecordMapper;

    @Override
    public RetryerQueueRecord transform(RetryRecord retryRecord) {
        var interval = flowManager.getNextInterval(retryRecord);
        return retryRecordMapper.toRetryerQueueRecordBuilder(retryRecord)
                .setDeliveryAttempt(retryRecord.getDeliveryAttempt() + 1)
                .setInterval(interval)
                .setRedeliveryTimestamp(getRedeliveryTimestamp(interval))
                .build();
    }

    private static Timestamp getRedeliveryTimestamp(RetryInterval interval) {
        return fromSeconds(now().plusSeconds(interval.getDuration().getSeconds()).getEpochSecond());
    }
}
