package com.github.cyberpunkperson.retryer.router.domain.router.configuration;

import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;

import static java.util.Optional.ofNullable;
import static org.springframework.kafka.listener.ContainerProperties.AckMode.MANUAL_IMMEDIATE;


@Configuration(proxyBeanMethods = false)
class RouterKafkaConfiguration {

    @Bean
    @ConfigurationProperties("kafka.topic.router")
    KafkaProperties routerKafkaProperties() {
        return new KafkaProperties();
    }

    @Bean
    ConsumerFactory<byte[], byte[]> routerConsumerFactory(KafkaProperties routerKafkaProperties) {
        return new DefaultKafkaConsumerFactory<>(
                routerKafkaProperties.buildConsumerProperties(),
                new ByteArrayDeserializer(),
                new ByteArrayDeserializer()
        );
    }

    @Bean
    ProducerFactory<byte[], byte[]> routerProducerFactory(KafkaProperties routerKafkaProperties) {
        return new DefaultKafkaProducerFactory<>(
                routerKafkaProperties.buildProducerProperties(),
                new ByteArraySerializer(),
                new ByteArraySerializer()
        );
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<byte[], byte[]> retryContainerFactory(KafkaProperties routerKafkaProperties,
                                                                                  ConsumerFactory<byte[], byte[]> routerConsumerFactory) {
        var containerFactory = new ConcurrentKafkaListenerContainerFactory<byte[], byte[]>();
        containerFactory.setConsumerFactory(routerConsumerFactory);
        containerFactory.getContainerProperties().setAckMode(MANUAL_IMMEDIATE);

        var consumersCount = ofNullable(routerKafkaProperties.getListener().getConcurrency()).orElse(1);
        containerFactory.setConcurrency(consumersCount);
        return containerFactory;
    }
}
