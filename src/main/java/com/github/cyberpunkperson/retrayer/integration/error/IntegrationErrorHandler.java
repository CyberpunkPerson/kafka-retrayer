package com.github.cyberpunkperson.retrayer.integration.error;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.kafka.support.converter.ConversionException;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import static com.github.cyberpunkperson.retrayer.integration.IntegrationHeaders.getOperation;
import static com.github.cyberpunkperson.retrayer.integration.logger.MdcKey.FAILED_EVENT;
import static com.github.cyberpunkperson.retrayer.integration.logger.MdcKey.OPERATION_NAME;
import static java.util.Optional.ofNullable;

@Slf4j
@Component
class IntegrationErrorHandler implements MessageHandler {


    @Override
    public void handleMessage(Message<?> failedMessage) {

        if (failedMessage.getPayload() instanceof MessagingException exception) {

            ofNullable(exception.getFailedMessage())
                    .ifPresentOrElse(message -> logFailedMessage(exception, message),
                            () -> log.error("Integration error with cause:", exception.getCause()));

        } else if (failedMessage.getPayload() instanceof ConversionException exception) {
            ofNullable(exception.getRecord())
                    .ifPresentOrElse(record ->
                                    log.error("Failed to convert records {} with cause:",
                                            record,
                                            exception.getCause()),

                            () -> log.error("Failed to convert records {} with cause:",
                                    exception.getRecords(),
                                    exception.getCause()));
        } else
            log.error("Integration error {}", failedMessage.getPayload());
    }

    private void logFailedMessage(MessagingException exception, Message<?> message) {
        MDC.put(OPERATION_NAME, getOperationCodeFromHeaders(message));
        MDC.put(FAILED_EVENT, message.getPayload().toString());
        log.error(buildLogMessage(message), exception.getCause());
        MDC.clear();
    }

    private static String buildLogMessage(Message<?> message) {
        return "Integration error - for %s operation with cause:".formatted(getOperationCodeFromHeaders(message));
    }

    private static String getOperationCodeFromHeaders(Message<?> message) {
        return getOperation(message.getHeaders());
    }
}