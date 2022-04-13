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

    private final Map<Duration, RetryInterval> intervals;
    private final Map<String, List<RetryInterval>> flows;


    RetryProperties(Map<Duration, RetryInterval> intervals, Map<String, List<RetryInterval>> flows) {
        isTrue(flows.containsKey(DEFAULT_FLOW), "Default flow should to be specified.");
        this.intervals = intervals;
        this.flows = flows;
    }

    public List<RetryInterval> getFlow(String flowName) {
        return flows.getOrDefault(flowName, flows.get(DEFAULT_FLOW));
    }

    public RetryInterval getInterval(String flowName, int deliveryAttempt) {
        var flow = getFlow(flowName);
        isTrue(deliveryAttempt < flow.size() && deliveryAttempt > 0, "Delivery attempt is out flow scope");
        return getFlow(flowName).get(deliveryAttempt);
    }

    public record RetryInterval(Duration duration, String topic) {
    }
}
