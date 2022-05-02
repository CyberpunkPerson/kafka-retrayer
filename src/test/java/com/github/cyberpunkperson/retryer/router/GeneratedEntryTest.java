package com.github.cyberpunkperson.retryer.router;

import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Durations;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import src.main.java.com.github.cyberpunkperson.retryer.router.Retryer.RetryEntry;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerRouter.LoopEntry;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerRouter.LoopEntry.RetryInterval;

import javax.xml.bind.DatatypeConverter;

import static src.main.java.com.github.cyberpunkperson.retryer.router.Retryer.RetryEntry.Flow.DEFAULT;

@SpringJUnitConfig
class GeneratedEntryTest { //todo remove

    @Test
    void generateRetryEntry() {
        var retryEntry = RetryEntry.newBuilder()
                .setApplicationName("postman")
                .setGroupId("GroupId")
                .setTopic("trash")
                .setOffset(2342)
                .setPartition(34234)
                .setTimestamp(Timestamp.getDefaultInstance())
                .setKey(ByteString.copyFromUtf8("Some key"))
                .setValue(ByteString.copyFromUtf8("Some value"))
                .setFlow(DEFAULT)
                .setDeliveryAttempt(0)
                .setErrorTimestamp(Timestamp.getDefaultInstance())
                .setErrorMessage("Error message")
                .build();
        var hexKey = DatatypeConverter.printHexBinary(retryEntry.getKey().toByteArray());
        var hexEntry = DatatypeConverter.printHexBinary(retryEntry.toByteArray());
        System.out.printf("Stop");
    }

    @Test
    void generateLoopEntry() {
        var loopEntry = LoopEntry.newBuilder()
                .setApplicationName("postman")
                .setGroupId("GroupId")
                .setTopic("trash")
                .setOffset(2342)
                .setPartition(34234)
                .setTimestamp(Timestamp.getDefaultInstance())
                .setKey(ByteString.copyFromUtf8("Some key"))
                .setValue(ByteString.copyFromUtf8("Some value"))
                .setInterval(RetryInterval.newBuilder()
                        .setTopic("retryer.interval.PT5M")
                        .setDuration(Durations.fromMinutes(5))
                        .build())
                .setDeliveryAttempt(0)
                .setErrorTimestamp(Timestamp.getDefaultInstance())
                .setErrorMessage("Error message")
                .build();
        var hexKey = DatatypeConverter.printHexBinary(loopEntry.getKey().toByteArray());
        var hexEntry = DatatypeConverter.printHexBinary(loopEntry.toByteArray());
        System.out.printf("Stop");
    }
}
