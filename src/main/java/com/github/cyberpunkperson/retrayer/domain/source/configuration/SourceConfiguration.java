package com.github.cyberpunkperson.retrayer.domain.source.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.messaging.MessageChannel;

import static org.springframework.integration.dsl.IntegrationFlows.from;
import static org.springframework.integration.dsl.MessageChannels.publishSubscribe;

@Configuration
@EnableIntegration
class SourceConfiguration {

    @Bean
    KafkaMessageDrivenChannelAdapter<byte[], byte[]> inboundSourceChannelAdapter(ConcurrentMessageListenerContainer<byte[], byte[]> sourceContainer,
                                                                                 MessageChannel integrationErrorChannel) {
        var inboundAdapter = new KafkaMessageDrivenChannelAdapter<>(sourceContainer);
        inboundAdapter.setPayloadType(byte[].class);
        inboundAdapter.setBindSourceRecord(true);
        inboundAdapter.setErrorChannel(integrationErrorChannel);
        return inboundAdapter;
    }

    @Bean
    MessageChannel inboundSourceChannel() {
        return publishSubscribe().get();
    }

    @Bean
    IntegrationFlow inboundSourceFlow(KafkaMessageDrivenChannelAdapter<byte[], byte[]> inboundSourceChannelAdapter) {
        return from(inboundSourceChannelAdapter)
                .handle(message -> {
                    System.out.println("Stop");
                })
                .get();
    }
}
