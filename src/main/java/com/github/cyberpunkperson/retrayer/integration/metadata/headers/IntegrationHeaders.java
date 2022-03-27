package com.github.cyberpunkperson.retrayer.integration.metadata.headers;

import lombok.experimental.UtilityClass;
import org.springframework.messaging.MessageHeaders;

import static com.github.cyberpunkperson.retrayer.integration.logger.MdcKey.OPERATION_NAME;

@UtilityClass
public class IntegrationHeaders {

    public static String getOperation(MessageHeaders headers) {
        return headers.get(OPERATION_NAME, String.class);
    }
}
