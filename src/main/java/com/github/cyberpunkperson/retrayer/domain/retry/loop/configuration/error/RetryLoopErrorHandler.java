package com.github.cyberpunkperson.retrayer.domain.retry.loop.configuration.error;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component("retryLoopErrorHandler")
class RetryLoopErrorHandler implements MessageHandler {

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        System.out.println("Handled"); //tood impl
    }
}
