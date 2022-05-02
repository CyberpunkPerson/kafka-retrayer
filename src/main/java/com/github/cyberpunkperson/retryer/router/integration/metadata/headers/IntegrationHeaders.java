package com.github.cyberpunkperson.retryer.router.integration.metadata.headers;

import lombok.experimental.UtilityClass;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import java.util.function.Function;

import static com.github.cyberpunkperson.retryer.router.integration.logger.MdcKey.OPERATION_NAME;

@UtilityClass
public class IntegrationHeaders { //todo combine with RetryHeaders?

    public static final String DEFAULT_FLOW = "default";


    public static String getOperation(MessageHeaders headers) {
        return headers.get(OPERATION_NAME, String.class);
    }

    public static Function<Message<byte[]>, String> extractTopic(String topicHeader) {
        return message -> message.getHeaders().get(topicHeader, String.class);
    }

    public static Function<Message<byte[]>, byte[]> extractMessageFey(String messageKeyHeader) {
        return message -> message.getHeaders().get(messageKeyHeader, byte[].class);
    }

//    public static Map<String, ?> convertHeaders(RetryRecord retryRecord) { todo reuse
//        return Map.of(
//                SOURCE_RECORD_APPLICATION_NAME, retryRecord.applicationName(),
//                SOURCE_RECORD_KEY, retryRecord.key(),
//                SOURCE_RECORD_TIMESTAMP, retryRecord.timestamp(),
//                SOURCE_RECORD_TOPIC, retryRecord.topic(),
//                SOURCE_RECORD_PARTITION, retryRecord.partition(),
//                SOURCE_RECORD_OFFSET, retryRecord.offset(),
//                SOURCE_RECORD_GROUP_ID, retryRecord.groupId(),
//                SOURCE_RECORD_ERROR_TIMESTAMP, retryRecord.errorTimestamp(),
//                SOURCE_RECORD_ERROR_MESSAGE, retryRecord.errorMessage()
//        );
//    }
}
