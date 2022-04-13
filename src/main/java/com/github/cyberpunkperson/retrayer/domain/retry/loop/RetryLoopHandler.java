package com.github.cyberpunkperson.retrayer.domain.retry.loop;

import com.github.cyberpunkperson.retrayer.domain.retry.configuration.properties.RetryProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;


@RequiredArgsConstructor
@Component("retryLoopHandler")
class RetryLoopHandler implements GenericHandler<LoopEntry> {

    private final RetryProperties retryProperties;
    private final Map<Duration, KafkaMessageDrivenChannelAdapter<byte[], byte[]>> retryAdapters;

    @Override
    public LoopEntry handle(LoopEntry retryEntry, MessageHeaders headers) {

        if (retryEntry.redeliveryTimestamp().isAfter(Instant.now())) {
            //todo pause thread/adapter or both
        }

        return null; //todo impl!
    }
}

