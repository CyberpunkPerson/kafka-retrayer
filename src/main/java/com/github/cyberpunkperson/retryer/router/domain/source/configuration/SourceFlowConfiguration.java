package com.github.cyberpunkperson.retryer.router.domain.source.configuration;

import com.github.cyberpunkperson.retryer.router.configuration.ChannelBuilder;
import com.github.cyberpunkperson.retryer.router.configuration.converter.ProtoMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.messaging.MessageChannel;
import src.main.java.com.github.cyberpunkperson.retryer.router.Retryer.RetryEntry;

import static org.springframework.integration.dsl.IntegrationFlows.from;

@EnableIntegration
@Configuration(proxyBeanMethods = false)
class SourceFlowConfiguration {

    @Bean
    KafkaMessageDrivenChannelAdapter<byte[], byte[]> inboundSourceChannelAdapter(ConcurrentMessageListenerContainer<byte[], byte[]> sourceContainer,
                                                                                 MessageChannel errorChannel) {
        var inboundAdapter = new KafkaMessageDrivenChannelAdapter<>(sourceContainer);
        inboundAdapter.setPayloadType(byte[].class);
        inboundAdapter.setBindSourceRecord(true);
        inboundAdapter.setErrorChannel(errorChannel);
        inboundAdapter.setMessageConverter(new ProtoMessageConverter<>(RetryEntry.parser()));
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
