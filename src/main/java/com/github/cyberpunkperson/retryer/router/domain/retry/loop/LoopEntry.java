package com.github.cyberpunkperson.retryer.router.domain.retry.loop;

import com.github.cyberpunkperson.retryer.router.domain.retry.configuration.properties.RetryProperties.RetryInterval;

import java.time.Instant;

public record LoopEntry(String applicationName, String groupId, String topic, long offset, int partition,
                        Instant timestamp, byte[] key, byte[] value, int deliveryAttempt, Instant redeliveryTimestamp,
                        RetryInterval interval, Instant errorTimestamp, String errorMessage) {
//    todo what is the purpose of errorTimestamp?
}
