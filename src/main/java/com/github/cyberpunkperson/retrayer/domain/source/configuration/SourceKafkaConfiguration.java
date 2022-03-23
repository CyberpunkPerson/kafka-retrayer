package com.github.cyberpunkperson.retrayer.domain.source.configuration;

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

@EnableKafka
@Configuration
class SourceKafkaConfiguration {

    @Bean
    @ConfigurationProperties("kafka.topic.source")
    KafkaProperties sourceConsumerProperties() {
        return new KafkaProperties();
    }

    @Bean
    ConsumerFactory<byte[], byte[]> sourceConsumerFactory(KafkaProperties sourceConsumerProperties) {
        return new DefaultKafkaConsumerFactory<>(sourceConsumerProperties.buildConsumerProperties());
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<byte[], byte[]> sourceContainerFactory(ConsumerFactory<byte[], byte[]> sourceConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<byte[], byte[]> container = new ConcurrentKafkaListenerContainerFactory<>();
        container.setConsumerFactory(sourceConsumerFactory);
        container.setConcurrency(1);
        return container;
    }

    @Bean
    ConcurrentMessageListenerContainer<byte[], byte[]> sourceContainer(@Value("${kafka.topic.source.names}") String[] names,
                                                                       ConcurrentKafkaListenerContainerFactory<byte[], byte[]> sourceContainerFactory) {
        return sourceContainerFactory.createContainer(names);
    }
}
