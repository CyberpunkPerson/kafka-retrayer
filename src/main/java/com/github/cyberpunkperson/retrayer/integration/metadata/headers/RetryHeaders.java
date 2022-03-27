package com.github.cyberpunkperson.retrayer.integration.metadata.headers;

import lombok.experimental.UtilityClass;

import java.util.Set;

@UtilityClass
public class RetryHeaders {
    public static final String SOURCE_RECORD_METADATA = "retry.source.record.metadata";
    public static final String SOURCE_RECORD_TIMESTAMP = "retry.source.record.timestamp";
    public static final String SOURCE_RECORD_GROUP_ID = "retry.source.record.group.id";
    public static final String RECORD_REDELIVERY_ATTEMPTS = "retry.redelivery.attempts";
    public static final String RETRY_INTERVAL = "retry.interval.s";
    public static final String SOURCE_RECORD_TOPIC = "retry.source.record.topic";
    public static final String SOURCE_RECORD_OFFSET = "retry.source.record.offset";
    public static final String SOURCE_RECORD_PARTITION = "retry.source.record.partition";
    public static final String SOURCE_RECORD_ERROR_MESSAGE = "retry.source.record.error.message";
    public static final String SOURCE_RECORD_ERROR_TIMESTAMP = "retry.source.record.error.timestamp";

    public static final Set<String> REQUIRED_HEADERS = Set.of(
            RETRY_INTERVAL,
            SOURCE_RECORD_TIMESTAMP,
            SOURCE_RECORD_TOPIC,
            SOURCE_RECORD_PARTITION,
            SOURCE_RECORD_OFFSET,
            SOURCE_RECORD_GROUP_ID,
            SOURCE_RECORD_ERROR_TIMESTAMP,
            SOURCE_RECORD_ERROR_MESSAGE
    );
}
