package com.github.cyberpunkperson.retryer.router.domain.retry.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.integration.kafka.dsl.KafkaProducerMessageHandlerSpec;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.Acknowledgment;

import static com.github.cyberpunkperson.retryer.router.integration.metadata.headers.IntegrationHeaders.*;
import static java.util.Optional.ofNullable;
import static org.springframework.kafka.support.KafkaHeaders.ACKNOWLEDGMENT;

@Configuration(proxyBeanMethods = false)
class RetryConfiguration {

    @Bean
    KafkaProducerMessageHandlerSpec<byte[], byte[], ?> outboundRetryChannelAdapter(ProducerFactory<byte[], byte[]> retryProducerFactory) {
        return Kafka
                .outboundChannelAdapter(retryProducerFactory)
                .topic(extractStringHeader(ENTRY_TOPIC))
                .messageKey(extractMessageFey(ENTRY_KEY))
                .flush(message ->
                        ofNullable(message.getHeaders().get(ACKNOWLEDGMENT, Acknowledgment.class))
                                .map(acknowledgment -> {
                                    acknowledgment.acknowledge();
                                    return true;
                                })
                                .orElse(false));
    }
}
