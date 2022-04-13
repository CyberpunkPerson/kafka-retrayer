package com.github.cyberpunkperson.retrayer.domain.source;

import com.github.cyberpunkperson.retrayer.domain.retry.configuration.properties.RetryProperties;
import com.github.cyberpunkperson.retrayer.domain.retry.flow.RetryEntry;
import com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.support.converter.MessagingMessageConverter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.HashSet;

import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.IntegrationHeaders.DEFAULT_FLOW;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.RECORD_REDELIVERY_ATTEMPTS;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.REQUIRED_HEADERS;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.SOURCE_RECORD_APPLICATION_NAME;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.SOURCE_RECORD_ERROR_MESSAGE;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.SOURCE_RECORD_ERROR_TIMESTAMP;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.SOURCE_RECORD_FLOW;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.SOURCE_RECORD_GROUP_ID;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.SOURCE_RECORD_OFFSET;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.SOURCE_RECORD_PARTITION;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.SOURCE_RECORD_TIMESTAMP;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.SOURCE_RECORD_TOPIC;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.instant;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.integer;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.longInt;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.string;
import static java.util.Optional.ofNullable;
import static org.springframework.util.Assert.isTrue;

@Component
@RequiredArgsConstructor
class SourceMessageConverter extends MessagingMessageConverter {

    private final RetryProperties retryProperties;

    @Override
    protected RetryEntry extractAndConvertValue(ConsumerRecord<?, ?> record, Type type) {
        var sourceHeaders = record.headers();
        var requiredHeaders = new HashSet<>(REQUIRED_HEADERS);
        sourceHeaders.forEach(header -> requiredHeaders.remove(header.key()));

        isTrue(requiredHeaders.isEmpty(), "Required headers are missing");

        var applicationName = string(sourceHeaders.lastHeader(SOURCE_RECORD_APPLICATION_NAME));
        var recordGroupId = string(sourceHeaders.lastHeader(SOURCE_RECORD_GROUP_ID));
        var recordTopic = string(sourceHeaders.lastHeader(SOURCE_RECORD_TOPIC));
        var recordOffset = longInt(sourceHeaders.lastHeader(SOURCE_RECORD_OFFSET));
        var recordPartition = integer(sourceHeaders.lastHeader(SOURCE_RECORD_PARTITION));
        var recordTimestamp = instant(sourceHeaders.lastHeader(SOURCE_RECORD_TIMESTAMP));
        var deliveryAttempt = getDeliveryAttempts(sourceHeaders);
        var recordErrorTimestamp = instant(sourceHeaders.lastHeader(SOURCE_RECORD_ERROR_TIMESTAMP));
        var recordErrorMessage = string(sourceHeaders.lastHeader(SOURCE_RECORD_ERROR_MESSAGE));
        var retryFlow = getRecordFlow(sourceHeaders);

        return new RetryEntry(applicationName,
                recordGroupId, recordTopic, recordOffset, recordPartition, recordTimestamp,
                (byte[]) record.key(), (byte[]) record.value(), retryFlow,
                deliveryAttempt, null , recordErrorTimestamp, recordErrorMessage //todo refactor
        );
    }

    public static int getDeliveryAttempts(Headers headers) {
        return ofNullable(headers.lastHeader(RECORD_REDELIVERY_ATTEMPTS))
                .map(RetryHeaders::integer)
                .orElse(0);
    }

    private String getRecordFlow(Headers headers) { //todo set default flow if absent in the outbound channel
        return ofNullable(headers.lastHeader(SOURCE_RECORD_FLOW))
                .map(RetryHeaders::string)
                .filter(flowName -> retryProperties.getFlows().containsKey(flowName))
                .orElse(DEFAULT_FLOW);
    }
}
