package com.github.cyberpunkperson.retryer.router.domain.retry.configuration.properties;

import com.google.protobuf.Duration;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerRouter.RetryerQueueRecord.RetryInterval;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerRouter.RetryerQueueRecord.RetryInterval.Builder;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerSource.RetryRecord.Flow;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static java.util.stream.Collectors.toMap;
import static org.springframework.util.Assert.isTrue;
import static src.main.java.com.github.cyberpunkperson.retryer.router.RetryerSource.RetryRecord.Flow.DEFAULT;

@Getter
@ConstructorBinding
@ConfigurationProperties("retry")
public class RetryProperties {

    private final Map<Duration, RetryInterval> intervals;
    private final Map<Flow, List<RetryInterval>> flows;


    RetryProperties(Map<Duration, RetryInterval.Builder> intervals, Map<Flow, List<RetryInterval.Builder>> flows) {
        isTrue(flows.containsKey(DEFAULT), "Default flow should to be specified.");
        this.intervals = intervals.entrySet().stream()
                .collect(toMap(Entry::getKey, entry -> entry.getValue().build()));
        this.flows = flows.entrySet().stream()
                .collect(toMap(Entry::getKey, entry -> entry.getValue().stream().map(Builder::build).toList()));
    }

    public List<RetryInterval> getFlow(Flow flowName) {
        return flows.getOrDefault(flowName, flows.get(DEFAULT));
    }

    public RetryInterval getInterval(Flow flowName, int deliveryAttempt) {
        var flow = getFlow(flowName);
        isTrue(deliveryAttempt < flow.size() && deliveryAttempt > 0, "Delivery attempt is out flow scope");
        return getFlow(flowName).get(deliveryAttempt);
    }
}
