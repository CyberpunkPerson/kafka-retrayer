package com.github.cyberpunkperson.retrayer.domain.source;

import com.github.cyberpunkperson.retrayer.domain.RecordMetadata;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.support.AbstractKafkaHeaderMapper;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Map;

import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.RECORD_REDELIVERY_ATTEMPTS;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.REQUIRED_HEADERS;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.RETRY_INTERVAL;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.SOURCE_RECORD_ERROR_MESSAGE;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.SOURCE_RECORD_ERROR_TIMESTAMP;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.SOURCE_RECORD_GROUP_ID;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.SOURCE_RECORD_METADATA;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.SOURCE_RECORD_OFFSET;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.SOURCE_RECORD_PARTITION;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.SOURCE_RECORD_TIMESTAMP;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.SOURCE_RECORD_TOPIC;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.isNull;

@Component
class SourceHeaderMapper extends AbstractKafkaHeaderMapper {

    @Override
    public void fromHeaders(MessageHeaders headers, Headers target) {
        headers.forEach((key, value) -> {
            if (!key.equals("kafka_deliveryAttempt")) {
                Object valueToAdd = this.headerValueToAddOut(key, value);
                if (valueToAdd instanceof byte[] && this.matches(key, valueToAdd)) {
                    target.add(new RecordHeader(key, (byte[]) valueToAdd));
                }
            }
        });
    }

    @Override
    public void toHeaders(Headers source, Map<String, Object> target) {
        var requiredHeaders = new HashSet<>(REQUIRED_HEADERS);
        source.forEach(header -> requiredHeaders.remove(header.key()));

        if (!requiredHeaders.isEmpty())
            throw new IllegalArgumentException("Required headers are missing");

        var retryInterval = integer(source.lastHeader(RETRY_INTERVAL));
        var recordPartition = integer(source.lastHeader(SOURCE_RECORD_PARTITION));
        var recordTimestamp = instant(source.lastHeader(SOURCE_RECORD_TIMESTAMP));
        var recordErrorTimestamp = instant(source.lastHeader(SOURCE_RECORD_ERROR_TIMESTAMP));
        var recordTopic = string(source.lastHeader(SOURCE_RECORD_TOPIC));
        var recordGroupId = string(source.lastHeader(SOURCE_RECORD_GROUP_ID));
        var recordErrorMessage = string(source.lastHeader(SOURCE_RECORD_ERROR_MESSAGE));
        var recordOffset = longInt(source.lastHeader(SOURCE_RECORD_OFFSET));
        var deliveryAttempts = getDeliveryAttempts(source);

        var metadata = new RecordMetadata(recordGroupId, recordTopic, recordTimestamp,
                retryInterval, recordPartition, recordOffset,
                deliveryAttempts, recordErrorTimestamp, recordErrorMessage);

        target.put(SOURCE_RECORD_METADATA, metadata);
    }

    private static int getDeliveryAttempts(Headers source) {
        if (isNull(source.lastHeader(RECORD_REDELIVERY_ATTEMPTS)))
            return 0;
        else
            return integer(source.lastHeader(RECORD_REDELIVERY_ATTEMPTS));
    }

    private static long longInt(Header header) {
        return Long.parseLong(new String(header.value(), UTF_8));
    }

    private static int integer(Header header) {
        return Integer.parseInt(new String(header.value(), UTF_8));
    }

    private static Instant instant(Header header) {
        try {
            return DateTimeFormatter.ISO_INSTANT.parse(new String(header.value(), UTF_8), Instant::from);
        } catch (DateTimeParseException e) {
            var ms = Long.parseLong(new String(header.value()));
            return Instant.ofEpochMilli(ms);
        }
    }

    private static String string(Header header) {
        return new String(header.value(), UTF_8);
    }
}
