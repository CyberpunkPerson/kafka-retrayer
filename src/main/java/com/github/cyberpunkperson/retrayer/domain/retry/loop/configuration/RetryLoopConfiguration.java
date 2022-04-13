package com.github.cyberpunkperson.retrayer.domain.retry.loop.configuration;

import com.github.cyberpunkperson.retrayer.domain.retry.configuration.properties.RetryProperties;
import com.github.cyberpunkperson.retrayer.domain.retry.configuration.properties.RetryProperties.RetryInterval;
import com.github.cyberpunkperson.retrayer.domain.retry.flow.RetryEntry;
import com.github.cyberpunkperson.retrayer.domain.retry.loop.LoopEntry;
import com.github.cyberpunkperson.retrayer.integration.ChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.messaging.MessageChannel;

import java.time.Duration;
import java.util.Map;

import static java.util.stream.Collectors.toMap;
import static org.springframework.integration.dsl.IntegrationFlows.from;
import static org.springframework.integration.dsl.MessageChannels.queue;

@Configuration(proxyBeanMethods = false)
class RetryLoopConfiguration {

    @Bean
    Map<Duration, KafkaMessageDrivenChannelAdapter<byte[], byte[]>> retryAdapters(RetryProperties retryProperties,
                                                                                  ConcurrentKafkaListenerContainerFactory<byte[], byte[]> retryContainerFactory,
                                                                                  MessageChannel inboundSourceChannel,
                                                                                  MessageChannel integrationErrorChannel,
                                                                                  RecordMessageConverter sourceMessageConverter) {
        return retryProperties.getIntervals().values().stream()
                .collect(toMap(RetryInterval::duration, retryInterval ->
                        createRetryAdapter(
                                retryContainerFactory.createContainer(retryInterval.topic()),
                                inboundSourceChannel,
                                integrationErrorChannel,
                                sourceMessageConverter
                        )));
    }

    private KafkaMessageDrivenChannelAdapter<byte[], byte[]> createRetryAdapter(ConcurrentMessageListenerContainer<byte[], byte[]> container,
                                                                                MessageChannel outputChannel,
                                                                                MessageChannel integrationErrorChannel,
                                                                                RecordMessageConverter sourceMessageConverter) {
        var inboundAdapter = new KafkaMessageDrivenChannelAdapter<>(container);
        inboundAdapter.setPayloadType(byte[].class);
        inboundAdapter.setBindSourceRecord(true);
        inboundAdapter.setOutputChannel(outputChannel);
        inboundAdapter.setErrorChannel(integrationErrorChannel);
        inboundAdapter.setMessageConverter(sourceMessageConverter);
        return inboundAdapter;
    }

    @Bean
    MessageChannel retryLoopChannel(ChannelBuilder channelBuilder) { //todo double check
        return queue().get();
    }

    @Bean
    IntegrationFlow retryLoopFlow(MessageChannel retryLoopChannel,
                                  GenericHandler<LoopEntry> retryLoopHandler,
                                  MessageChannel outboundRetryChannel) {
        return from(retryLoopChannel)
                .handle(retryLoopHandler)
                .channel(outboundRetryChannel)
                .get();
    }
}
