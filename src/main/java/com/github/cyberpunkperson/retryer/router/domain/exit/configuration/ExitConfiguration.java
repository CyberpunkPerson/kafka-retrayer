package com.github.cyberpunkperson.retryer.router.domain.exit.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.integration.kafka.dsl.KafkaProducerMessageHandlerSpec;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.Message;

import java.util.function.Function;

import static com.github.cyberpunkperson.retryer.router.support.header.InternalHeader.*;
import static java.util.Optional.ofNullable;
import static org.springframework.kafka.support.KafkaHeaders.ACKNOWLEDGMENT;

@Configuration(proxyBeanMethods = false)
class ExitConfiguration {

    @Bean
    KafkaProducerMessageHandlerSpec<byte[], byte[], ?>exitChannelAdapter(ProducerFactory<byte[], byte[]> exitProducerFactory) {
        return Kafka
                .outboundChannelAdapter(exitProducerFactory)
                .topic(extractHeader(RECORD_TOPIC, String.class))
                .messageKey(extractHeader(RECORD_KEY, byte[].class))
                .flush(acknowledge());
    }

    private static Function<Message<Object>, Boolean> acknowledge() {
        return message ->
                ofNullable(message.getHeaders().get(ACKNOWLEDGMENT, Acknowledgment.class))
                        .map(acknowledgment -> {
                            acknowledgment.acknowledge();
                            return true;
                        })
                        .orElse(false);
    }
}
