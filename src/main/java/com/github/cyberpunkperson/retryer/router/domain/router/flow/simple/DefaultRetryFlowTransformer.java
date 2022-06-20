package com.github.cyberpunkperson.retryer.router.domain.router.flow.simple;

import com.github.cyberpunkperson.retryer.router.domain.router.flow.FlowManager;
import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.stereotype.Component;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerRouter.RouterQueueRecord;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerRouter.RouterQueueRecord.RetryDelay;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerSource.RetryRecord;

import static com.google.protobuf.util.Timestamps.fromSeconds;
import static java.time.Instant.now;


@Component
@RequiredArgsConstructor
class DefaultRetryFlowTransformer implements GenericTransformer<RetryRecord, RouterQueueRecord> {

    private final FlowManager flowManager;
    private final RetryRecordMapper retryRecordMapper;

    @Override
    public RouterQueueRecord transform(RetryRecord retryRecord) {
        var delay = flowManager.getNextInterval(retryRecord);
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
