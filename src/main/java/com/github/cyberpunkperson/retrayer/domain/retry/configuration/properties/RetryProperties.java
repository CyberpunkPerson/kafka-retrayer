package com.github.cyberpunkperson.retrayer.domain.retry.configuration.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static com.github.cyberpunkperson.retrayer.integration.metadata.headers.IntegrationHeaders.DEFAULT_FLOW;
import static org.springframework.util.Assert.isTrue;

@Getter
@ConstructorBinding
@ConfigurationProperties("retry")
public class RetryProperties {

    private final Map<String, List<Duration>> flows;


    RetryProperties(Map<String, List<Duration>> flows) {
        isTrue(flows.containsKey(DEFAULT_FLOW), "Default flow should to be specified");
        this.flows = flows;
    }

    public List<Duration> getFlow(String flowName) {
        return flows.getOrDefault(flowName, flows.get(DEFAULT_FLOW));
    }
}
