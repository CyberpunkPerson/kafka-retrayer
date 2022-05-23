package com.github.cyberpunkperson.retryer.router.domain.archive.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.core.GenericSelector;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.kafka.dsl.KafkaProducerMessageHandlerSpec;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.messaging.MessageChannel;
import src.main.java.com.github.cyberpunkperson.retryer.router.Retryer.RetryEntry;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerArchive.ArchiveEntry;

import java.util.function.Function;

import static com.github.cyberpunkperson.retryer.router.integration.metadata.headers.IntegrationHeaders.*;
import static org.springframework.integration.dsl.IntegrationFlows.from;

@Configuration(proxyBeanMethods = false)
public class ArchiveFlowConfiguration {

    @Bean
    IntegrationFlow archiveFlow(MessageChannel inboundSourceChannel,
                                GenericSelector<RetryEntry> archiveFlowFilter,
                                GenericTransformer<RetryEntry, ArchiveEntry> archiveFlowTransformer,
                                @Value("${archive.topic}") String archiveTopic,
                                KafkaProducerMessageHandlerSpec<byte[], byte[], ?> outboundRetryChannelAdapter) {
        return from(inboundSourceChannel)
                .filter(archiveFlowFilter)
                .transform(archiveFlowTransformer)
                .enrichHeaders(extract(ENTRY_KEY, (Function<ArchiveEntry, byte[]>) entry -> entry.getApplicationName().getBytes()))
                .enrichHeaders(enricher -> enricher.header(ENTRY_TOPIC, archiveTopic))
                .transform(ArchiveEntry::toByteArray)
                .handle(outboundRetryChannelAdapter)
                .get();
    }
}
