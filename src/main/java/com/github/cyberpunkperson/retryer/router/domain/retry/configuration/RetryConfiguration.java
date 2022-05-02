package com.github.cyberpunkperson.retryer.router.domain.retry.configuration;

import com.github.cyberpunkperson.retryer.router.configuration.ChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.integration.kafka.dsl.KafkaProducerMessageHandlerSpec;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.messaging.MessageChannel;

import static com.github.cyberpunkperson.retryer.router.integration.metadata.headers.IntegrationHeaders.extractMessageFey;
import static com.github.cyberpunkperson.retryer.router.integration.metadata.headers.IntegrationHeaders.extractTopic;
import static com.github.cyberpunkperson.retryer.router.integration.metadata.headers.RetryHeaders.SOURCE_RECORD_KEY;
import static com.github.cyberpunkperson.retryer.router.integration.metadata.headers.RetryHeaders.SOURCE_RECORD_TOPIC;
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
                .topic(extractTopic(SOURCE_RECORD_TOPIC))
                .messageKey(extractMessageFey(SOURCE_RECORD_KEY));
//                .flush(); todo flush acknowledgement manual
    }

    @Bean
    IntegrationFlow outboundRetryFlow(MessageChannel outboundRetryChannel,
                                      KafkaProducerMessageHandlerSpec<byte[], byte[], ?> outboundRetryChannelAdapter) {
        return from(outboundRetryChannel)
                .handle(outboundRetryChannelAdapter)
                .get();
    }
}
