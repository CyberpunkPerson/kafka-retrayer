package com.github.cyberpunkperson.retrayer.integration.metadata.headers;

import com.github.cyberpunkperson.retrayer.domain.retry.flow.RecordFlowContext;
import lombok.experimental.UtilityClass;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import java.util.function.Function;

import static com.github.cyberpunkperson.retrayer.integration.logger.MdcKey.OPERATION_NAME;

@UtilityClass
public class IntegrationHeaders { //todo combine with RetryHeaders?

    public static final String RECORD_CONTEXT = "record.context";
    public static final String DEFAULT_FLOW = "default";


    public static String getOperation(MessageHeaders headers) {
        return headers.get(OPERATION_NAME, String.class);
    }

    public static RecordFlowContext getRecordFlowContext(MessageHeaders headers) {
        return headers.get(RECORD_CONTEXT, RecordFlowContext.class);
    }

    public static Function<Message<Object>, String> determinateTopic() {
        return message -> ""; //todo impl
    }
}
