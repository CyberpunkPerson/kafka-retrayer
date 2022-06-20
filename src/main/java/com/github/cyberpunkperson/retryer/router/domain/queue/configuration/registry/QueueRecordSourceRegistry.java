package com.github.cyberpunkperson.retryer.router.domain.queue.configuration.registry;

import com.github.cyberpunkperson.retryer.router.domain.queue.configuration.properties.QueueProperties;
import com.google.protobuf.Duration;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.kafka.inbound.KafkaMessageSource;
import org.springframework.stereotype.Service;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Service
public class QueueRecordSourceRegistry<K, V> {

    private final Map<Duration, KafkaMessageSource<K, V>> queueRecordSources;


    public QueueRecordSourceRegistry(QueueProperties queueProperties,
                                     QueueRecordSourceFactory<K, V> queueRecordSourceFactory,
                                     IntegrationFlowContext context) {
        this.queueRecordSources = queueProperties.getDelays().values().stream()
                .map(retryDelay -> {
                    var bundle = queueRecordSourceFactory.createMessageSourceFlow(retryDelay);
                    context.registration(bundle.flow())
                            .id(bundle.flow().getBeanName())
                            .useFlowIdAsPrefix()
                            .register();
                    return bundle;
                })
                .collect(toMap(bundle -> bundle.delay().getDuration(), QueueBundle::source));
    }

    public KafkaMessageSource<K, V> getSource(Duration delay) {
        return queueRecordSources.get(delay);
    }
}
