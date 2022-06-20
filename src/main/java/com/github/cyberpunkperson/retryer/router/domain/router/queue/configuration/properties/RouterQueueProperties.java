package com.github.cyberpunkperson.retryer.router.domain.router.queue.configuration.properties;

import com.google.protobuf.Duration;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerRouter.RouterQueueRecord.RetryDelay;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerRouter.RouterQueueRecord.RetryDelay.Builder;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerSource.RetryRecord.Flow;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static java.util.stream.Collectors.toMap;
import static org.springframework.util.Assert.isTrue;
import static src.main.java.com.github.cyberpunkperson.retryer.router.RetryerSource.RetryRecord.Flow.DEFAULT;

@Getter
@ConstructorBinding
@ConfigurationProperties("router.queue")
public class RouterQueueProperties {

    private final Map<Duration, RetryDelay> delays;
    private final Map<Flow, List<RetryDelay>> flows;


    RouterQueueProperties(Map<Duration, RetryDelay.Builder> delays, Map<Flow, List<RetryDelay.Builder>> flows) {
        isTrue(flows.containsKey(DEFAULT), "Default flow should to be specified.");
        this.delays = delays.entrySet().stream()
                .collect(toMap(Entry::getKey, entry -> entry.getValue().build()));
        this.flows = flows.entrySet().stream()
                .collect(toMap(Entry::getKey, entry -> entry.getValue().stream().map(Builder::build).toList()));
    }

    public List<RetryDelay> getFlow(Flow flowName) {
        return flows.getOrDefault(flowName, flows.get(DEFAULT));
    }

    public RetryDelay getDelay(Flow flowName, int deliveryAttempt) {
        var flow = getFlow(flowName);
        isTrue(deliveryAttempt < flow.size() && deliveryAttempt > 0, "Delivery attempt is out flow scope");
        return getFlow(flowName).get(deliveryAttempt);
    }
}
