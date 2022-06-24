package com.github.cyberpunkperson.retryer.router.domain.exit.configuration;

import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;

@Configuration(proxyBeanMethods = false)
class ExitKafkaConfiguration {

    @Bean
    @ConfigurationProperties("kafka.topic.exit")
    KafkaProperties exitKafkaProperties() {
        return new KafkaProperties();
    }

    @Bean
    ProducerFactory<byte[], byte[]> exitProducerFactory(KafkaProperties exitKafkaProperties) {
        return new DefaultKafkaProducerFactory<>(
                exitKafkaProperties.buildProducerProperties(),
                new ByteArraySerializer(),
                new ByteArraySerializer()
        );
    }
}
