package com.github.cyberpunkperson.retryer.router.support.header;

import com.github.cyberpunkperson.retryer.router.support.constant.MdcKey;
import lombok.experimental.UtilityClass;
import org.springframework.integration.dsl.HeaderEnricherSpec;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import java.util.function.Consumer;
import java.util.function.Function;

@UtilityClass
public class InternalHeader {

    public static final String RECORD_KEY = "record.key";
    public static final String RECORD_TOPIC = "record.topic";


    public static String getOperation(MessageHeaders headers) {
        return headers.get(MdcKey.OPERATION_NAME, String.class);
    }

    public static <T, R> Consumer<HeaderEnricherSpec> extract(String key, Function<T, R> func) {
        return enricher -> enricher.<T>headerFunction(key, message -> func.apply(message.getPayload()));
    }

    public static <T> Function<Message<byte[]>, T> extractHeader(String header, Class<T> type) {
        return message -> message.getHeaders().get(header, type);
    }
}
