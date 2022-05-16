package com.github.cyberpunkperson.retryer.router.domain.retry.loop.configuration.error;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.kafka.inbound.KafkaMessageSource.KafkaAckCallback;
import org.springframework.kafka.support.converter.ConversionException;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import static java.util.Optional.ofNullable;
import static org.springframework.integration.IntegrationMessageHeaderAccessor.ACKNOWLEDGMENT_CALLBACK;
import static org.springframework.integration.acks.AcknowledgmentCallback.Status.REQUEUE;

@Slf4j
@RequiredArgsConstructor
@Component("retryLoopErrorHandler")
class RetryLoopErrorHandler implements MessageHandler {

    @Override
    public void handleMessage(Message<?> failedMessage) throws MessagingException {
        if (failedMessage.getPayload() instanceof MessagingException exception) {
            ofNullable(exception.getFailedMessage())
                    .ifPresentOrElse(this::handleLoopEntry,
                            () -> log.error("Loop entry handle error occur", exception.getCause()));

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

    private void handleLoopEntry(Message<?> entry) { //todo handle with MDC
        var acknowledgmentCallback = entry.getHeaders().get(ACKNOWLEDGMENT_CALLBACK, KafkaAckCallback.class);
        acknowledgmentCallback.acknowledge(REQUEUE);
    }
}
