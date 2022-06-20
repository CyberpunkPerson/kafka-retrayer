package com.github.cyberpunkperson.retryer.router.domain.router.queue.configuration.registry;

import com.github.cyberpunkperson.retryer.router.domain.router.queue.configuration.properties.RouterQueueProperties;
import com.google.protobuf.Duration;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.kafka.inbound.KafkaMessageSource;
import org.springframework.stereotype.Service;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Service
public class RouterQueueRecordSourceRegistry<K, V> {

    private final Map<Duration, KafkaMessageSource<K, V>> routerQueueRecordSources;


    public RouterQueueRecordSourceRegistry(RouterQueueProperties routerQueueProperties,
                                           RouterQueueRecordSourceFactory<K, V> routerQueueRecordSourceFactory,
                                           IntegrationFlowContext context) {
        this.routerQueueRecordSources = routerQueueProperties.getDelays().values().stream()
                .map(retryDelay -> {
                    var bundle = routerQueueRecordSourceFactory.createMessageSourceFlow(retryDelay);
                    context.registration(bundle.flow())
                            .id(bundle.flow().getBeanName())
                            .useFlowIdAsPrefix()
                            .register();
                    return bundle;
                })
                .collect(toMap(bundle -> bundle.delay().getDuration(), RouterFlowBundle::source));
    }

    public KafkaMessageSource<K, V> getSource(Duration delay) {
        return routerQueueRecordSources.get(delay);
    }
}
