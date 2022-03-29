package com.github.cyberpunkperson.retrayer.domain.retry.flow;

import java.time.Instant;

public record RecordFlowContext(String applicationName, String groupId, String topic, long offset, int partition,
                                Instant timestamp, byte[] key, int deliveryAttempt, String retryFlow,
                                Instant errorTimestamp, String errorMessage) {
}
