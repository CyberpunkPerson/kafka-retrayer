package com.github.cyberpunkperson.retrayer.domain.retry.flow.simple;

import com.github.cyberpunkperson.retrayer.domain.retry.RetryRecord;
import com.github.cyberpunkperson.retrayer.domain.retry.configuration.properties.RetryProperties;
import com.github.cyberpunkperson.retrayer.domain.retry.flow.simple.service.RetryRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.IntegrationHeaders.getRecordFlowContext;
import static java.time.Instant.now;
import static java.util.UUID.randomUUID;

@Component
@RequiredArgsConstructor
class DefaultRetryHandler implements MessageHandler {

    private final RetryProperties retryProperties;
    private final RetryRecordService retryRecordService;


    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        var recordContext = getRecordFlowContext(message.getHeaders());
        var delay = retryProperties.getFlow(recordContext.retryFlow()).get(recordContext.deliveryAttempt());
        var redeliveryTimestamp = now().plus(delay);

        var record = new RetryRecord(randomUUID(), recordContext.applicationName(),
                recordContext.groupId(), recordContext.topic(), recordContext.offset(), recordContext.partition(),
                recordContext.timestamp(), recordContext.key(), (byte[]) message.getPayload(),
                recordContext.deliveryAttempt() + 1, recordContext.retryFlow(),
                redeliveryTimestamp, recordContext.errorTimestamp(), recordContext.errorMessage()
        );

        retryRecordService.save(record);
    }
}
