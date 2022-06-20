package com.github.cyberpunkperson.retryer.router.domain.queue.configuration.registry;

import org.springframework.integration.dsl.StandardIntegrationFlow;
import org.springframework.integration.kafka.inbound.KafkaMessageSource;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerQueue.QueueRecord.RetryDelay;

record QueueBundle<K, V>(RetryDelay delay,
                         KafkaMessageSource<K, V> source,
                         StandardIntegrationFlow flow) {
}
