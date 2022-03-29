package com.github.cyberpunkperson.retrayer.domain.retry.configuration;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;

@Configuration(proxyBeanMethods = false)
class RetryKafkaConfiguration {

    @Bean
    @ConfigurationProperties("kafka.topic.retry")
    KafkaProperties retryProducerProperties() {
        return new KafkaProperties();
    }

    @Bean
    ProducerFactory<byte[], byte[]> retryProducerFactory(KafkaProperties retryProducerProperties) {
        return new DefaultKafkaProducerFactory<>(retryProducerProperties.buildProducerProperties());
    }
}
