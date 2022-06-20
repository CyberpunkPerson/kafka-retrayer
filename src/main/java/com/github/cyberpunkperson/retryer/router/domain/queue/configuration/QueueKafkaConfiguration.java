package com.github.cyberpunkperson.retryer.router.domain.queue.configuration;

import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import static java.util.Optional.ofNullable;
import static org.springframework.kafka.listener.ContainerProperties.AckMode.MANUAL_IMMEDIATE;


@Configuration(proxyBeanMethods = false)
class QueueKafkaConfiguration {

    @Bean
    @ConfigurationProperties("kafka.topic.queue")
    KafkaProperties queueKafkaProperties() {
        return new KafkaProperties();
    }

    @Bean
    ConsumerFactory<byte[], byte[]> queueConsumerFactory(KafkaProperties queueKafkaProperties) {
        return new DefaultKafkaConsumerFactory<>(
                queueKafkaProperties.buildConsumerProperties(),
                new ByteArrayDeserializer(),
                new ByteArrayDeserializer()
        );
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<byte[], byte[]> queueContainerFactory(KafkaProperties queueKafkaProperties,
                                                                                  ConsumerFactory<byte[], byte[]> queueConsumerFactory) {
        var containerFactory = new ConcurrentKafkaListenerContainerFactory<byte[], byte[]>();
        containerFactory.setConsumerFactory(queueConsumerFactory);
        containerFactory.getContainerProperties().setAckMode(MANUAL_IMMEDIATE);

        var consumersCount = ofNullable(queueKafkaProperties.getListener().getConcurrency()).orElse(1);
        containerFactory.setConcurrency(consumersCount);
        return containerFactory;
    }
}
