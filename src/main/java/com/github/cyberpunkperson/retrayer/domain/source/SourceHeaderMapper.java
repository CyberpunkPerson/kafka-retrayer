package com.github.cyberpunkperson.retrayer.domain.source;

import com.github.cyberpunkperson.retrayer.domain.retry.configuration.properties.RetryProperties;
import com.github.cyberpunkperson.retrayer.domain.retry.flow.RecordFlowContext;
import com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.support.AbstractKafkaHeaderMapper;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;

import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.IntegrationHeaders.DEFAULT_FLOW;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.IntegrationHeaders.RECORD_CONTEXT;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.REQUIRED_HEADERS;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.SOURCE_RECORD_APPLICATION_NAME;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.SOURCE_RECORD_ERROR_MESSAGE;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.SOURCE_RECORD_ERROR_TIMESTAMP;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.SOURCE_RECORD_FLOW;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.SOURCE_RECORD_GROUP_ID;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.SOURCE_RECORD_KEY;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.SOURCE_RECORD_OFFSET;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.SOURCE_RECORD_PARTITION;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.SOURCE_RECORD_TIMESTAMP;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.SOURCE_RECORD_TOPIC;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.getDeliveryAttempts;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.instant;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.integer;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.longInt;
import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.RetryHeaders.string;
import static java.util.Optional.ofNullable;
import static org.springframework.util.Assert.isTrue;

@Component
@RequiredArgsConstructor
class SourceHeaderMapper extends AbstractKafkaHeaderMapper {

    private final RetryProperties retryProperties;

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

        isTrue(requiredHeaders.isEmpty(), "Required headers are missing");

        var applicationName = string(source.lastHeader(SOURCE_RECORD_APPLICATION_NAME));
        var recordGroupId = string(source.lastHeader(SOURCE_RECORD_GROUP_ID));
        var recordTopic = string(source.lastHeader(SOURCE_RECORD_TOPIC));
        var recordOffset = longInt(source.lastHeader(SOURCE_RECORD_OFFSET));
        var recordPartition = integer(source.lastHeader(SOURCE_RECORD_PARTITION));
        var recordTimestamp = instant(source.lastHeader(SOURCE_RECORD_TIMESTAMP));
        var recordKey = source.lastHeader(SOURCE_RECORD_KEY).value(); //todo get record key!
        var deliveryAttempt = getDeliveryAttempts(source);
        var recordErrorTimestamp = instant(source.lastHeader(SOURCE_RECORD_ERROR_TIMESTAMP));
        var recordErrorMessage = string(source.lastHeader(SOURCE_RECORD_ERROR_MESSAGE));
        var retryFlow = getRecordFlow(source);

        var context = new RecordFlowContext(applicationName,
                recordGroupId, recordTopic, recordOffset, recordPartition, recordTimestamp, recordKey,
                deliveryAttempt, retryFlow, recordErrorTimestamp, recordErrorMessage
        );

        target.put(RECORD_CONTEXT, context);
    }

    private String getRecordFlow(Headers headers) { //todo set default flow if absent in the outbound channel
        return ofNullable(headers.lastHeader(SOURCE_RECORD_FLOW))
                .map(RetryHeaders::string)
                .filter(flowName -> retryProperties.getFlows().containsKey(flowName))
                .orElse(DEFAULT_FLOW);
    }
}
