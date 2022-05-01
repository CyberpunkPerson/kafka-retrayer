package com.github.cyberpunkperson.retrayer.domain.retry.loop.configuration.registry;

import com.github.cyberpunkperson.retrayer.domain.retry.configuration.properties.RetryProperties.RetryInterval;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class RetryLoopAdapterFactory<K, V> {

    private final MessageChannel retryLoopChannel;
    private final MessageChannel retryLoopErrorChannel;
    private final RecordMessageConverter retryMessageConverter;
    private final ConcurrentKafkaListenerContainerFactory<K, V> retryContainerFactory;

    private static final String CONTAINER_SUFFIX = "RetryContainer";
    private static final String ADAPTER_SUFFIX = "RetryAdapter";


    public KafkaMessageDrivenChannelAdapter<K, V> createRetryAdapter(RetryInterval retryInterval) {
        var container = retryContainerFactory.createContainer(retryInterval.topic());
        container.setBeanName(buildBeanName(retryInterval, CONTAINER_SUFFIX));

        var adapter = new KafkaMessageDrivenChannelAdapter<>(container);
        adapter.setPayloadType(byte[].class);
        adapter.setBindSourceRecord(true);
        adapter.setOutputChannel(retryLoopChannel);
        adapter.setErrorChannel(retryLoopErrorChannel);
        adapter.setMessageConverter(retryMessageConverter);

        adapter.setBeanName(buildBeanName(retryInterval, ADAPTER_SUFFIX));
        return adapter;
    }

    private static String buildBeanName(RetryInterval retryInterval, String suffix) {
        return retryInterval.duration().toString().toLowerCase() + suffix;
    }
}
