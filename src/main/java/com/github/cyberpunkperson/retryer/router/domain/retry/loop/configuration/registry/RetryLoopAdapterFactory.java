package com.github.cyberpunkperson.retryer.router.domain.retry.loop.configuration.registry;

import com.github.cyberpunkperson.retryer.router.configuration.converter.ProtoMessageConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerRouter.LoopEntry;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerRouter.LoopEntry.RetryInterval;

import java.time.Duration;

@Service
@RequiredArgsConstructor
class RetryLoopAdapterFactory<K, V> {

    private final MessageChannel retryLoopChannel;
    private final MessageChannel retryLoopErrorChannel;
    private final ConcurrentKafkaListenerContainerFactory<K, V> retryContainerFactory;

    private static final String CONTAINER_SUFFIX = "RetryContainer";
    private static final String ADAPTER_SUFFIX = "RetryAdapter";


    public KafkaMessageDrivenChannelAdapter<K, V> createRetryAdapter(RetryInterval retryInterval) {
        var container = retryContainerFactory.createContainer(retryInterval.getTopic());
        container.setBeanName(buildBeanName(retryInterval, CONTAINER_SUFFIX));

        var adapter = new KafkaMessageDrivenChannelAdapter<>(container);
        adapter.setPayloadType(byte[].class);
        adapter.setOutputChannel(retryLoopChannel);
        adapter.setErrorChannel(retryLoopErrorChannel);
        adapter.setMessageConverter(new ProtoMessageConverter<>(LoopEntry.parser()));

        adapter.setBeanName(buildBeanName(retryInterval, ADAPTER_SUFFIX));
        return adapter;
    }

    private static String buildBeanName(RetryInterval retryInterval, String suffix) {
        return Duration.ofSeconds(retryInterval.getDuration().getSeconds()).toString().toLowerCase() + suffix;
    }
}
