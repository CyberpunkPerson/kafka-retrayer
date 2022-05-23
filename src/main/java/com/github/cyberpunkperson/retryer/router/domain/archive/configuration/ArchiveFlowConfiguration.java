package com.github.cyberpunkperson.retryer.router.domain.archive.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.core.GenericSelector;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.kafka.dsl.KafkaProducerMessageHandlerSpec;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.messaging.MessageChannel;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerArchive.ArchiveRecord;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerSource.RetryRecord;

import java.util.function.Function;

import static com.github.cyberpunkperson.retryer.router.support.headers.InternalHeaders.*;
import static org.springframework.integration.dsl.IntegrationFlows.from;

@Configuration(proxyBeanMethods = false)
public class ArchiveFlowConfiguration {

    @Bean
    IntegrationFlow archiveFlow(MessageChannel inboundSourceChannel,
                                GenericSelector<RetryRecord> archiveFlowFilter,
                                GenericTransformer<RetryRecord, ArchiveRecord> archiveFlowTransformer,
                                @Value("${archive.topic}") String archiveTopic,
                                KafkaProducerMessageHandlerSpec<byte[], byte[], ?> outboundChannelAdapter) {
        return from(inboundSourceChannel)
                .filter(archiveFlowFilter)
                .transform(archiveFlowTransformer)
                .enrichHeaders(extract(RECORD_KEY, (Function<ArchiveRecord, byte[]>) record -> record.getApplicationName().getBytes()))
                .enrichHeaders(enricher -> enricher.header(RECORD_TOPIC, archiveTopic))
                .transform(ArchiveRecord::toByteArray)
                .handle(outboundChannelAdapter)
                .get();
    }
}
