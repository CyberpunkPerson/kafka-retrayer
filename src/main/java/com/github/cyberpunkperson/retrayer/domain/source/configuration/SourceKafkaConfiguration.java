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

import static java.util.Optional.ofNullable;

@EnableKafka
@Configuration(proxyBeanMethods = false)
class SourceKafkaConfiguration {

    @Bean
    @ConfigurationProperties("kafka.topic.source")
    KafkaProperties sourceTopicProperties() {
        return new KafkaProperties();
    }

    @Bean
    ConsumerFactory<byte[], byte[]> sourceConsumerFactory(KafkaProperties sourceTopicProperties) {
        return new DefaultKafkaConsumerFactory<>(sourceTopicProperties.buildConsumerProperties());
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<byte[], byte[]> sourceContainerFactory(KafkaProperties sourceTopicProperties,
                                                                                   ConsumerFactory<byte[], byte[]> sourceConsumerFactory) {
        var containerFactory = new ConcurrentKafkaListenerContainerFactory<byte[], byte[]>();
        containerFactory.setConsumerFactory(sourceConsumerFactory);

        var consumersCount = ofNullable(sourceTopicProperties.getListener().getConcurrency()).orElse(1);
        containerFactory.setConcurrency(consumersCount);
        return containerFactory;
    }

    @Bean
    ConcurrentMessageListenerContainer<byte[], byte[]> sourceContainer(@Value("${kafka.topic.source.names}") String[] names,
                                                                       ConcurrentKafkaListenerContainerFactory<byte[], byte[]> sourceContainerFactory) {
        return sourceContainerFactory.createContainer(names);
    }
}
