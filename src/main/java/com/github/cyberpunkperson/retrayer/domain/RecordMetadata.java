package com.github.cyberpunkperson.retrayer.domain;

import java.time.Instant;

public record RecordMetadata(String groupId, String topic, Instant timestamp,
                             int interval, int partition, long offset, int deliveryAttempt,
                             Instant errorTimestamp, String errorMessage) {
}
