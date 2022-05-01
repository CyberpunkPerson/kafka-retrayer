package com.github.cyberpunkperson.retrayer.configuration;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.channel.MessagePublishingErrorHandler;
import org.springframework.integration.dsl.PublishSubscribeChannelSpec;
import org.springframework.stereotype.Component;

import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.springframework.integration.dsl.MessageChannels.publishSubscribe;
import static org.springframework.util.Assert.hasText;

@Component
@RequiredArgsConstructor
public class ChannelBuilder {

    private final MessagePublishingErrorHandler defaultMessagePublishingErrorHandler;


    public PublishSubscribeChannelSpec<?> publishSubscribeChannel(String threadNameFormat) {
        hasText(threadNameFormat, "Thread name format should be specified");
        var threadFactory = new ThreadFactoryBuilder()
                .setNameFormat(threadNameFormat)
                .build();
        return publishSubscribe(newSingleThreadExecutor(threadFactory))
                .errorHandler(defaultMessagePublishingErrorHandler);
    }
}
