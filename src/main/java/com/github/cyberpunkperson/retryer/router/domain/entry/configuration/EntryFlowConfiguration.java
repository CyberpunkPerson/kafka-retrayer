package com.github.cyberpunkperson.retryer.router.domain.entry.configuration;

import com.github.cyberpunkperson.retryer.router.configuration.ChannelBuilder;
import com.github.cyberpunkperson.retryer.router.configuration.converter.ProtoMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.messaging.MessageChannel;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerEntry.RetryRecord;

import static org.springframework.integration.dsl.IntegrationFlows.from;

@EnableIntegration
@Configuration(proxyBeanMethods = false)
class EntryFlowConfiguration {

    @Bean
    KafkaMessageDrivenChannelAdapter<byte[], byte[]> entrySourceChannelAdapter(ConcurrentMessageListenerContainer<byte[], byte[]> entryContainer,
                                                                                 MessageChannel errorChannel) {
        var inboundAdapter = new KafkaMessageDrivenChannelAdapter<>(entryContainer);
        inboundAdapter.setPayloadType(byte[].class);
        inboundAdapter.setBindSourceRecord(true);
        inboundAdapter.setErrorChannel(errorChannel);
        inboundAdapter.setMessageConverter(new ProtoMessageConverter<>(RetryRecord.parser()));
        return inboundAdapter;
    }

    @Bean
    MessageChannel entryChannel(ChannelBuilder channelBuilder) {
        return channelBuilder
                .publishSubscribeChannel("entryChannel-%d")
                .get();
    }

    @Bean
    IntegrationFlow entryFlow(KafkaMessageDrivenChannelAdapter<byte[], byte[]> entryChannelAdapter,
                                      MessageChannel entryChannel) {
        return from(entryChannelAdapter)
                .channel(entryChannel)
                .get();
    }
}
