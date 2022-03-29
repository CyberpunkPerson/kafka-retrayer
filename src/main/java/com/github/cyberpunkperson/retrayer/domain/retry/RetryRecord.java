package com.github.cyberpunkperson.retrayer.domain.retry;

import org.springframework.data.relational.core.mapping.Column;

import java.time.Instant;
import java.util.UUID;

public record RetryRecord(UUID id, String applicationName, String groupId, String topic,
                          @Column("record_offset") long offset, @Column("record_partition") int partition,
                          @Column("record_timestamp") Instant timestamp, @Column("record_key") byte[] key,
                          @Column("record_payload") byte[] payload, int deliveryAttempt, String flow,
                          Instant redeliveryTimestamp, Instant errorTimestamp, String errorMessage) {
}
