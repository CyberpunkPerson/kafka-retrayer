package com.github.cyberpunkperson.retrayer.domain.retry.configuration;

import com.github.cyberpunkperson.retrayer.integration.ChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.integration.kafka.dsl.KafkaProducerMessageHandlerSpec;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.messaging.MessageChannel;

import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.IntegrationHeaders.determinateTopic;
import static org.springframework.integration.dsl.IntegrationFlows.from;

@Configuration(proxyBeanMethods = false)
class RetryConfiguration {

    @Bean
    MessageChannel outboundRetryChannel(ChannelBuilder channelBuilder) {
        return channelBuilder
                .publishSubscribeChannel("outboundRetryChannel-%d")
                .get();
    }

    @Bean
    KafkaProducerMessageHandlerSpec<byte[], byte[], ?> outboundRetryChannelAdapter(ProducerFactory<byte[], byte[]> retryProducerFactory) {
        return Kafka
                .outboundChannelAdapter(retryProducerFactory)
                .topic(determinateTopic())
                ; //todo messageKey(message -> ?)
    }

    @Bean
    IntegrationFlow outboundRetryFlow(MessageChannel outboundRetryChannel,
                                      KafkaProducerMessageHandlerSpec<byte[], byte[], ?> outboundRetryChannelAdapter) {
        return from(outboundRetryChannel)
                .handle(outboundRetryChannelAdapter)
                .get();
    }
}
