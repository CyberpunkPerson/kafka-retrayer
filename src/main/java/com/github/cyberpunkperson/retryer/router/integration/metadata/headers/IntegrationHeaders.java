package com.github.cyberpunkperson.retryer.router.integration.metadata.headers;

import lombok.experimental.UtilityClass;
import org.springframework.integration.dsl.HeaderEnricherSpec;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import java.util.function.Consumer;
import java.util.function.Function;

import static com.github.cyberpunkperson.retryer.router.integration.logger.MdcKey.OPERATION_NAME;

@UtilityClass
public class IntegrationHeaders { //todo combine with RetryHeaders?

    public static final String DEFAULT_FLOW = "default";
    public static final String ENTRY_KEY = "entry.key"; //todo loop message?
    public static final String ENTRY_TOPIC = "entry.topic"; //todo loop message?


    public static String getOperation(MessageHeaders headers) {
        return headers.get(OPERATION_NAME, String.class);
    }

    public static <T, R> Consumer<HeaderEnricherSpec> extract(String key, Function<T, R> func) {
        return enricher -> enricher.<T>headerFunction(key, message -> func.apply(message.getPayload()));
    }

    public static <T> Function<Message<byte[]>, T> extractHeader(String header, Class<T> type) {
        return message -> message.getHeaders().get(header, type);
    }

//    public static Map<String, ?> convertHeaders(RetryRecord retryRecord) { todo reuse
//        return Map.of(
//                SOURCE_RECORD_APPLICATION_NAME, retryRecord.applicationName(),
//                SOURCE_RECORD_KEY, retryRecord.key(),
//                SOURCE_RECORD_TIMESTAMP, retryRecord.timestamp(),
//                SOURCE_RECORD_TOPIC, retryRecord.retryTopic(),
//                SOURCE_RECORD_PARTITION, retryRecord.partition(),
//                SOURCE_RECORD_OFFSET, retryRecord.offset(),
//                SOURCE_RECORD_GROUP_ID, retryRecord.groupId(),
//                SOURCE_RECORD_ERROR_TIMESTAMP, retryRecord.errorTimestamp(),
//                SOURCE_RECORD_ERROR_MESSAGE, retryRecord.errorMessage()
//        );
//    }
}
