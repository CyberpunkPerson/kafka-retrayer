package com.github.cyberpunkperson.retrayer.domain.retry.configuration;

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
class RetryKafkaConfiguration {

    @Bean
    @ConfigurationProperties("kafka.topic.retry")
    KafkaProperties retryTopicProperties() {
        return new KafkaProperties();
    }

    @Bean
    ConsumerFactory<byte[], byte[]> retryConsumerFactory(KafkaProperties retryTopicProperties) {
        return new DefaultKafkaConsumerFactory<>(retryTopicProperties.buildConsumerProperties());
    }

    @Bean
    ProducerFactory<byte[], byte[]> retryProducerFactory(KafkaProperties retryTopicProperties) {
        return new DefaultKafkaProducerFactory<>(retryTopicProperties.buildProducerProperties());
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<byte[], byte[]> retryContainerFactory(KafkaProperties retryTopicProperties,
                                                                                  ConsumerFactory<byte[], byte[]> retryConsumerFactory) {
        var containerFactory = new ConcurrentKafkaListenerContainerFactory<byte[], byte[]>();
        containerFactory.setConsumerFactory(retryConsumerFactory);
        containerFactory.getContainerProperties().setAckMode(MANUAL_IMMEDIATE);

        var consumersCount = ofNullable(retryTopicProperties.getListener().getConcurrency()).orElse(1);
        containerFactory.setConcurrency(consumersCount);
        return containerFactory;
    }
}
