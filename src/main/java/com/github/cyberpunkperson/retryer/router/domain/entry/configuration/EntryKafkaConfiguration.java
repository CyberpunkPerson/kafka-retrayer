package com.github.cyberpunkperson.retryer.router.domain.entry.configuration;

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
class EntryKafkaConfiguration {

    @Bean
    @ConfigurationProperties("kafka.topic.entry")
    KafkaProperties entryKafkaProperties() {
        return new KafkaProperties();
    }

    @Bean
    ConsumerFactory<byte[], byte[]> entryConsumerFactory(KafkaProperties entryKafkaProperties) {
        return new DefaultKafkaConsumerFactory<>(
                entryKafkaProperties.buildConsumerProperties(),
                new ByteArrayDeserializer(),
                new ByteArrayDeserializer()
        );
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<byte[], byte[]> entryContainerFactory(KafkaProperties entryKafkaProperties,
                                                                                   ConsumerFactory<byte[], byte[]> entryConsumerFactory) {
        var containerFactory = new ConcurrentKafkaListenerContainerFactory<byte[], byte[]>();
        containerFactory.setConsumerFactory(entryConsumerFactory);
        containerFactory.getContainerProperties().setAckMode(MANUAL_IMMEDIATE);

        var consumersCount = ofNullable(entryKafkaProperties.getListener().getConcurrency()).orElse(1);
        containerFactory.setConcurrency(consumersCount);
        return containerFactory;
    }

    @Bean
    ConcurrentMessageListenerContainer<byte[], byte[]> entryContainer(@Value("${kafka.topic.entry.name}") String[] name,
                                                                       ConcurrentKafkaListenerContainerFactory<byte[], byte[]> entryContainerFactory) {
        return entryContainerFactory.createContainer(name);
    }
}
