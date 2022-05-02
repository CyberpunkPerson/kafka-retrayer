package com.github.cyberpunkperson.retryer.router.domain.retry.loop;

import com.github.cyberpunkperson.retryer.router.domain.retry.configuration.properties.RetryProperties;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import static java.time.Instant.now;


@RequiredArgsConstructor
@Component("retryLoopHandler")
class RetryLoopBarrier implements GenericHandler<LoopEntry> {

    private final RetryProperties retryProperties;
    private final Map<Duration, KafkaMessageDrivenChannelAdapter<byte[], byte[]>> retryAdapters;

    @Override
    public LoopEntry handle(LoopEntry retryEntry, MessageHeaders headers) {

        if (retryEntry.redeliveryTimestamp().isBefore(now())) {
            waitTillRedelivery(retryEntry.redeliveryTimestamp());
        }

        return retryEntry;
    }

    @SneakyThrows
    private static void waitTillRedelivery(Instant redeliveryTimestamp) {
        Thread.sleep(Math.min(500, now().minusMillis(redeliveryTimestamp.toEpochMilli()).toEpochMilli()));
    }
}

