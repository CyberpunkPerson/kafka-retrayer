package com.github.cyberpunkperson.retrayer.domain.retry.flow.simple;

import com.github.cyberpunkperson.retrayer.domain.retry.configuration.properties.RetryProperties;
import com.github.cyberpunkperson.retrayer.domain.retry.flow.RetryEntry;
import com.github.cyberpunkperson.retrayer.domain.retry.loop.LoopEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import static java.time.Instant.now;

@RequiredArgsConstructor
@Component("defaultRetryFlowHandler")
class DefaultRetryFlowHandler implements GenericHandler<RetryEntry> {

    private final RetryProperties retryProperties;

    @Override
    public LoopEntry handle(RetryEntry retryEntry, MessageHeaders headers) {
        var interval = retryProperties.getInterval(retryEntry.retryFlow(), retryEntry.deliveryAttempt());

        return new LoopEntry(retryEntry.applicationName(), retryEntry.groupId(), retryEntry.topic(),
                retryEntry.offset(), retryEntry.partition(), retryEntry.timestamp(), retryEntry.key(),
                retryEntry.value(), retryEntry.deliveryAttempt() + 1, now().plus(interval.duration()),
                interval, retryEntry.errorTimestamp(), retryEntry.errorMessage()
        );
    }
}
