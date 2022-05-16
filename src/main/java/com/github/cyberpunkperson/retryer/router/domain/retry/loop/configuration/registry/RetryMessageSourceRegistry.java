package com.github.cyberpunkperson.retryer.router.domain.retry.loop.configuration.registry;

import com.github.cyberpunkperson.retryer.router.domain.retry.configuration.properties.RetryProperties;
import com.google.protobuf.Duration;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.kafka.inbound.KafkaMessageSource;
import org.springframework.stereotype.Service;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Service
public class RetryMessageSourceRegistry<K, V> { //todo interface / generics?

    private final Map<Duration, KafkaMessageSource<K, V>> retryMessageSources;


    public RetryMessageSourceRegistry(RetryProperties retryProperties,
                                      RetryMessageSourceFactory<K, V> retryMessageSourceFactory,
                                      IntegrationFlowContext context) {
        this.retryMessageSources = retryProperties.getIntervals().values().stream()
                .map(retryInterval -> {
                    var bundle = retryMessageSourceFactory.createMessageSourceFlow(retryInterval);
                    context.registration(bundle.flow())
                            .id(bundle.flow().getBeanName())
                            .useFlowIdAsPrefix()
                            .register();
                    return bundle;
                })
                .collect(toMap(bundle -> bundle.interval().getDuration(), RetryFlowBundle::source));
    }

    public KafkaMessageSource<K, V> getSource(Duration interval) {
        return retryMessageSources.get(interval);
    }
}
