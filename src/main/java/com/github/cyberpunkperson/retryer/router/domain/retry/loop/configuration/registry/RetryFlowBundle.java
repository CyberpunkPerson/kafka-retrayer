package com.github.cyberpunkperson.retryer.router.domain.retry.loop.configuration.registry;

import org.springframework.integration.dsl.StandardIntegrationFlow;
import org.springframework.integration.kafka.inbound.KafkaMessageSource;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerRouter.LoopEntry.RetryInterval;

record RetryFlowBundle<K, V>(RetryInterval interval,
                             KafkaMessageSource<K, V> source,
                             StandardIntegrationFlow flow) {
}
