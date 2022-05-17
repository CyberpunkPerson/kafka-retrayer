package com.github.cyberpunkperson.retryer.router.domain.retry.flow.simple;

import com.github.cyberpunkperson.retryer.router.domain.retry.flow.FlowManager;
import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.stereotype.Component;
import src.main.java.com.github.cyberpunkperson.retryer.router.Retryer.RetryEntry;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerRouter.LoopEntry;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerRouter.LoopEntry.RetryInterval;

import static com.google.protobuf.util.Timestamps.fromSeconds;
import static java.time.Instant.now;


@Component
@RequiredArgsConstructor
class DefaultRetryFlowTransformer implements GenericTransformer<RetryEntry, LoopEntry> {

    private final FlowManager flowManager;
    private final LoopEntryMapper loopEntryMapper;

    @Override
    public LoopEntry transform(RetryEntry source) {
        var interval = flowManager.getNextInterval(source);
        return loopEntryMapper.toLoopEntryBuilder(source)
                .setDeliveryAttempt(source.getDeliveryAttempt() + 1)
                .setInterval(interval)
                .setRedeliveryTimestamp(getRedeliveryTimestamp(interval))
                .build();
    }

    private static Timestamp getRedeliveryTimestamp(RetryInterval interval) {
        return fromSeconds(now().plusSeconds(interval.getDuration().getSeconds()).getEpochSecond());
    }
}
