package com.github.cyberpunkperson.retrayer.domain.retry.loop.configuration;

import com.github.cyberpunkperson.retrayer.domain.retry.configuration.properties.RetryProperties;
import com.github.cyberpunkperson.retrayer.domain.retry.configuration.properties.RetryProperties.RetryInterval;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Service
class RetryLoopAdapterRegistry<K, V> {

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
                .collect(toMap(bundle -> bundle.interval().duration(), IntervalAdapterBundle::adapter));
    }

    private record IntervalAdapterBundle<K, V>(RetryInterval interval,
                                               KafkaMessageDrivenChannelAdapter<K, V> adapter) {
    }
}
