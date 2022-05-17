package com.github.cyberpunkperson.retryer.router.integration.metadata.headers;

import lombok.experimental.UtilityClass;
import org.apache.kafka.common.header.Header;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;

//todo compare with KafkaHeaders
@UtilityClass
public class RetryHeaders { //todo remove 'source' prefix?
    public static final String SOURCE_RECORD_APPLICATION_NAME = "retry.source.record.application.name";
    public static final String SOURCE_RECORD_GROUP_ID = "retry.source.record.group.id";
    public static final String SOURCE_RECORD_KEY = "retry.source.record.key";
    public static final String SOURCE_RECORD_TIMESTAMP = "retry.source.record.timestamp";
    public static final String SOURCE_RECORD_FLOW = "retry.source.record.flow";
    public static final String RECORD_REDELIVERY_ATTEMPTS = "retry.redelivery.attempts";
    public static final String REDELIVERY_RECORD_TIMESTAMP = "retry.redelivery.timestamp";
    public static final String SOURCE_RECORD_TOPIC = "retry.source.record.retryTopic";
    public static final String SOURCE_RECORD_OFFSET = "retry.source.record.offset";
    public static final String SOURCE_RECORD_PARTITION = "retry.source.record.partition";
    public static final String SOURCE_RECORD_ERROR_MESSAGE = "retry.source.record.error.message";
    public static final String SOURCE_RECORD_ERROR_TIMESTAMP = "retry.source.record.error.timestamp";

    public static final Set<String> REQUIRED_HEADERS = Set.of(
            SOURCE_RECORD_APPLICATION_NAME,
            SOURCE_RECORD_KEY,
            SOURCE_RECORD_TIMESTAMP,
            SOURCE_RECORD_TOPIC,
            SOURCE_RECORD_PARTITION,
            SOURCE_RECORD_OFFSET,
            SOURCE_RECORD_GROUP_ID,
            SOURCE_RECORD_ERROR_TIMESTAMP,
            SOURCE_RECORD_ERROR_MESSAGE
    );

    public static long longInt(Header header) {
        return Long.parseLong(new String(header.value(), UTF_8));
    }

    public static int integer(Header header) {
        return Integer.parseInt(new String(header.value(), UTF_8));
    }

    public static Instant instant(Header header) {
        try {
            return DateTimeFormatter.ISO_INSTANT.parse(new String(header.value(), UTF_8), Instant::from);
        } catch (DateTimeParseException e) {
            var ms = Long.parseLong(new String(header.value()));
            return Instant.ofEpochMilli(ms);
        }
    }

    public static String string(Header header) {
        return new String(header.value(), UTF_8);
    }
}
