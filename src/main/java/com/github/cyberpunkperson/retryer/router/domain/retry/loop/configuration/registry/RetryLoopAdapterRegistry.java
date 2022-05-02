package com.github.cyberpunkperson.retryer.router.domain.retry.loop.configuration.registry;

import com.github.cyberpunkperson.retryer.router.domain.retry.configuration.properties.RetryProperties;
import com.google.protobuf.Duration;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.stereotype.Service;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerRouter.LoopEntry.RetryInterval;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Service
class RetryLoopAdapterRegistry<K, V> { //todo interface

    private final RetryProperties retryProperties;
    private final Map<Duration, KafkaMessageDrivenChannelAdapter<K, V>> retryAdapters;


    public RetryLoopAdapterRegistry(RetryProperties retryProperties,
                                    ConfigurableBeanFactory beanFactory,
                                    RetryLoopAdapterFactory<K, V> retryLoopAdapterFactory) {
        this.retryAdapters = initializeAdapters(retryProperties, beanFactory, retryLoopAdapterFactory);
        this.retryProperties = retryProperties;
    }

    private Map<Duration, KafkaMessageDrivenChannelAdapter<K, V>> initializeAdapters(RetryProperties retryProperties,
                                                                                     ConfigurableBeanFactory beanFactory,
                                                                                     RetryLoopAdapterFactory<K, V> retryLoopAdapterFactory) {
        return retryProperties.getIntervals().values().stream()
                .map(retryInterval -> {
                    var adapter = retryLoopAdapterFactory.createRetryAdapter(retryInterval);
                    adapter.afterPropertiesSet();
                    beanFactory.registerSingleton(adapter.getBeanName(), adapter);
                    return new IntervalAdapterBundle<>(retryInterval, adapter);
                })
                .collect(toMap(bundle -> bundle.interval().getDuration(), IntervalAdapterBundle::adapter));
    }

    private record IntervalAdapterBundle<K, V>(RetryInterval interval,
                                               KafkaMessageDrivenChannelAdapter<K, V> adapter) {
    }
}
