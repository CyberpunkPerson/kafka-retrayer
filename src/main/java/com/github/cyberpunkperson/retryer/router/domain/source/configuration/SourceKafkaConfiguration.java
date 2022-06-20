package com.github.cyberpunkperson.retryer.router.domain.source.configuration;

import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import static java.util.Optional.ofNullable;
import static org.springframework.kafka.listener.ContainerProperties.AckMode.MANUAL_IMMEDIATE;

@EnableKafka
@Configuration(proxyBeanMethods = false)
class SourceKafkaConfiguration {

    @Bean
    @ConfigurationProperties("kafka.topic.source")
    KafkaProperties sourceKafkaProperties() {
        return new KafkaProperties();
    }

    @Bean
    ConsumerFactory<byte[], byte[]> sourceConsumerFactory(KafkaProperties sourceKafkaProperties) {
        return new DefaultKafkaConsumerFactory<>(
                sourceKafkaProperties.buildConsumerProperties(),
                new ByteArrayDeserializer(),
                new ByteArrayDeserializer()
        );
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<byte[], byte[]> sourceContainerFactory(KafkaProperties sourceKafkaProperties,
                                                                                   ConsumerFactory<byte[], byte[]> sourceConsumerFactory) {
        var containerFactory = new ConcurrentKafkaListenerContainerFactory<byte[], byte[]>();
        containerFactory.setConsumerFactory(sourceConsumerFactory);
        containerFactory.getContainerProperties().setAckMode(MANUAL_IMMEDIATE);

        var consumersCount = ofNullable(sourceKafkaProperties.getListener().getConcurrency()).orElse(1);
        containerFactory.setConcurrency(consumersCount);
        return containerFactory;
    }

    @Bean
    ConcurrentMessageListenerContainer<byte[], byte[]> sourceContainer(@Value("${kafka.topic.source.name}") String[] name,
                                                                       ConcurrentKafkaListenerContainerFactory<byte[], byte[]> sourceContainerFactory) {
        return sourceContainerFactory.createContainer(name);
    }
}
