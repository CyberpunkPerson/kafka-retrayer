package com.github.cyberpunkperson.retryer.router.domain.router.queue.configuration.registry;

import org.springframework.integration.dsl.StandardIntegrationFlow;
import org.springframework.integration.kafka.inbound.KafkaMessageSource;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerRouter.RouterQueueRecord.RetryDelay;

record RouterFlowBundle<K, V>(RetryDelay delay,
                              KafkaMessageSource<K, V> source,
                              StandardIntegrationFlow flow) {
}
