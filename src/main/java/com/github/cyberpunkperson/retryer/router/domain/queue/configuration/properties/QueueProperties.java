package com.github.cyberpunkperson.retryer.router.domain.queue.configuration.properties;

import com.google.protobuf.Duration;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerQueue.QueueRecord.RetryDelay;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerQueue.QueueRecord.RetryDelay.Builder;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerEntry.RetryRecord.Backoff;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static java.util.stream.Collectors.toMap;
import static org.springframework.util.Assert.isTrue;
import static src.main.java.com.github.cyberpunkperson.retryer.router.RetryerEntry.RetryRecord.Backoff.DEFAULT;

@Getter
@ConstructorBinding
@ConfigurationProperties("queue")
public class QueueProperties {

    private final Map<Duration, RetryDelay> delays;
    private final Map<Backoff, List<RetryDelay>> backoffs;


    QueueProperties(Map<Duration, RetryDelay.Builder> delays, Map<Backoff, List<RetryDelay.Builder>> backoffs) {
        isTrue(backoffs.containsKey(DEFAULT), "Default flow should to be specified.");
        this.delays = delays.entrySet().stream()
                .collect(toMap(Entry::getKey, entry -> entry.getValue().build()));
        this.backoffs = backoffs.entrySet().stream()
                .collect(toMap(Entry::getKey, entry -> entry.getValue().stream().map(Builder::build).toList()));
    }

    public List<RetryDelay> getBackoff(Backoff backoffName) {
        return backoffs.getOrDefault(backoffName, backoffs.get(DEFAULT));
    }

    public RetryDelay getDelay(Backoff backoffName, int deliveryAttempt) {
        var backoff = getBackoff(backoffName);
        isTrue(deliveryAttempt < backoff.size() && deliveryAttempt > 0, "Delivery attempt is out backoff scope");
        return getBackoff(backoffName).get(deliveryAttempt);
    }
}
