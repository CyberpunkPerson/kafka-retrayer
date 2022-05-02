package com.github.cyberpunkperson.retryer.router.domain.retry.flow;

import java.time.Instant;

public record RetryEntry(String applicationName, String groupId, String topic, long offset, int partition,
                         Instant timestamp, byte[] key, byte[] value, String retryFlow, int deliveryAttempt,
                         Instant redeliveryTimestamp, Instant errorTimestamp, String errorMessage) {
}
