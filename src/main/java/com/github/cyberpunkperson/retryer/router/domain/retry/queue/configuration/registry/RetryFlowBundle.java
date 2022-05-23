package com.github.cyberpunkperson.retryer.router.domain.retry.queue.configuration.registry;

import org.springframework.integration.dsl.StandardIntegrationFlow;
import org.springframework.integration.kafka.inbound.KafkaMessageSource;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerRouter.RetryerQueueRecord.RetryInterval;

record RetryFlowBundle<K, V>(RetryInterval interval,
                             KafkaMessageSource<K, V> source,
                             StandardIntegrationFlow flow) {
}
