package com.github.cyberpunkperson.retryer.router.domain.retry.queue.configuration.registry;

import com.github.cyberpunkperson.retryer.router.domain.retry.configuration.properties.RetryProperties;
import com.google.protobuf.Duration;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.kafka.inbound.KafkaMessageSource;
import org.springframework.stereotype.Service;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Service
public class RetryerQueueRecordSourceRegistry<K, V> {

    private final Map<Duration, KafkaMessageSource<K, V>> retryQueueRecordSources;


    public RetryerQueueRecordSourceRegistry(RetryProperties retryProperties,
                                            RetryerQueueRecordSourceFactory<K, V> retryerQueueRecordSourceFactory,
                                            IntegrationFlowContext context) {
        this.retryQueueRecordSources = retryProperties.getIntervals().values().stream()
                .map(retryInterval -> {
                    var bundle = retryerQueueRecordSourceFactory.createMessageSourceFlow(retryInterval);
                    context.registration(bundle.flow())
                            .id(bundle.flow().getBeanName())
                            .useFlowIdAsPrefix()
                            .register();
                    return bundle;
                })
                .collect(toMap(bundle -> bundle.interval().getDuration(), RetryFlowBundle::source));
    }

    public KafkaMessageSource<K, V> getSource(Duration interval) {
        return retryQueueRecordSources.get(interval);
    }
}
