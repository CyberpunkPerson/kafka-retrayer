package com.github.cyberpunkperson.retrayer.domain.source.configuration;

import com.github.cyberpunkperson.retrayer.integration.ChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.KafkaHeaderMapper;
import org.springframework.kafka.support.converter.MessagingMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.messaging.MessageChannel;

import static org.springframework.integration.dsl.IntegrationFlows.from;

@EnableIntegration
@Configuration(proxyBeanMethods = false)
class SourceFlowConfiguration {

    @Bean
    RecordMessageConverter sourceMessageConverter(KafkaHeaderMapper sourceHeaderMapper) {
        var messageConverter = new MessagingMessageConverter();
        messageConverter.setHeaderMapper(sourceHeaderMapper);
        return messageConverter;
    }

    @Bean
    KafkaMessageDrivenChannelAdapter<byte[], byte[]> inboundSourceChannelAdapter(ConcurrentMessageListenerContainer<byte[], byte[]> sourceContainer,
                                                                                 MessageChannel integrationErrorChannel,
                                                                                 RecordMessageConverter sourceMessageConverter) {
        var inboundAdapter = new KafkaMessageDrivenChannelAdapter<>(sourceContainer);
        inboundAdapter.setPayloadType(byte[].class);
        inboundAdapter.setBindSourceRecord(true);
        inboundAdapter.setErrorChannel(integrationErrorChannel);
        inboundAdapter.setMessageConverter(sourceMessageConverter);
        return inboundAdapter;
    }

    @Bean
    MessageChannel inboundSourceChannel(ChannelBuilder channelBuilder) {
        return channelBuilder
                .publishSubscribeChannel("inboundSourceChannel-%d")
                .get();
    }

    @Bean
    IntegrationFlow inboundSourceFlow(KafkaMessageDrivenChannelAdapter<byte[], byte[]> inboundSourceChannelAdapter,
                                      MessageChannel inboundSourceChannel) {
        return from(inboundSourceChannelAdapter)
                .channel(inboundSourceChannel)
                .get();
    }
}
