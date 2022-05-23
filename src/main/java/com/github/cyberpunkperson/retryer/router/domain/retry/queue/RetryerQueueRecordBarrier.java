package com.github.cyberpunkperson.retryer.router.domain.retry.queue;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerRouter.RetryerQueueRecord;

import java.time.Instant;

import static java.time.Instant.now;


@RequiredArgsConstructor
@Component("retryerQueueRecordBarrier") //todo remove?
class RetryerQueueRecordBarrier implements GenericHandler<RetryerQueueRecord> {

    @Override
    public RetryerQueueRecord handle(RetryerQueueRecord queueRecord, MessageHeaders headers) {

        var redeliveryTimestamp = Instant.ofEpochSecond(queueRecord.getRedeliveryTimestamp().getSeconds());
        if (redeliveryTimestamp.isAfter(now()))
            waitTillRedelivery(redeliveryTimestamp);

        return queueRecord;
    }

    @SneakyThrows
    private static void waitTillRedelivery(Instant redeliveryTimestamp) {
        var timeToSleep = redeliveryTimestamp.minusMillis(now().toEpochMilli()).toEpochMilli();
        if (timeToSleep > 0)
            Thread.sleep(Math.max(500, timeToSleep));
    }
}

