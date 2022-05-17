package com.github.cyberpunkperson.retryer.router.domain.retry.loop;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerRouter.LoopEntry;

import java.time.Instant;

import static java.time.Instant.now;


@RequiredArgsConstructor
@Component("retryLoopBarrier")
class RetryLoopBarrier implements GenericHandler<LoopEntry> {

    @Override
    public LoopEntry handle(LoopEntry retryEntry, MessageHeaders headers) {

        var redeliveryTimestamp = Instant.ofEpochSecond(retryEntry.getRedeliveryTimestamp().getSeconds());
        if (redeliveryTimestamp.isAfter(now()))
            waitTillRedelivery(redeliveryTimestamp);

        return retryEntry;
    }

    @SneakyThrows
    private static void waitTillRedelivery(Instant redeliveryTimestamp) {
        var timeToSleep = redeliveryTimestamp.minusMillis(now().toEpochMilli()).toEpochMilli();
        if (timeToSleep > 0)
            Thread.sleep(timeToSleep); //todo low border + should we turn off/on container?
    }
}

