package com.github.cyberpunkperson.retryer.router;

import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Durations;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerRouter.RouterQueueRecord;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerRouter.RouterQueueRecord.RetryDelay;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerSource.RetryRecord;

import javax.xml.bind.DatatypeConverter;

import static src.main.java.com.github.cyberpunkperson.retryer.router.RetryerSource.RetryRecord.Flow.DEFAULT;

@SpringJUnitConfig
class GeneratedEntryTest { //todo remove

    @Test
    void generateSourceRecord() {
        var sourceRecord = RetryRecord.newBuilder()
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
        var hexKey = DatatypeConverter.printHexBinary(sourceRecord.getKey().toByteArray());
//        536F6D65206B6579
        var hexEntry = DatatypeConverter.printHexBinary(sourceRecord.toByteArray());
//        0A07706F73746D616E120747726F757049641A05747261736820A61228BA8B0232003A08536F6D65206B6579420A536F6D652076616C75655A00620D4572726F72206D657373616765
        System.out.printf("Stop");
    }

    @Test
    void expiredSourceRecord() {
        var sourceRecord = RetryRecord.newBuilder()
                .setApplicationName("postman")
                .setGroupId("GroupId")
                .setTopic("trash")
                .setOffset(2342)
                .setPartition(34234)
                .setTimestamp(Timestamp.getDefaultInstance())
                .setKey(ByteString.copyFromUtf8("Some key"))
                .setValue(ByteString.copyFromUtf8("Some value"))
                .setFlow(DEFAULT)
                .setDeliveryAttempt(4)
                .setErrorTimestamp(Timestamp.getDefaultInstance())
                .setErrorMessage("Error message")
                .build();
        var hexKey = DatatypeConverter.printHexBinary(sourceRecord.getKey().toByteArray());
//        536F6D65206B6579
        var hexEntry = DatatypeConverter.printHexBinary(sourceRecord.toByteArray());
//        0A07706F73746D616E120747726F757049641A05747261736820A61228BA8B0232003A08536F6D65206B6579420A536F6D652076616C756550045A00620D4572726F72206D657373616765
        System.out.printf("Stop");
    }

    @Test
    void generateRetryRecord() {
        var retryRecord = RouterQueueRecord.newBuilder()
                .setApplicationName("postman")
                .setGroupId("GroupId")
                .setTopic("trash")
                .setOffset(2342)
                .setPartition(34234)
                .setTimestamp(Timestamp.getDefaultInstance())
                .setKey(ByteString.copyFromUtf8("Some key"))
                .setValue(ByteString.copyFromUtf8("Some value"))
                .setDelay(RetryDelay.newBuilder()
                        .setTopic("retryer.delay.PT5M")
                        .setDuration(Durations.fromMinutes(5))
                        .build())
                .setDeliveryAttempt(0)
                .setErrorTimestamp(Timestamp.getDefaultInstance())
                .setErrorMessage("Error message")
                .build();
        var hexKey = DatatypeConverter.printHexBinary(retryRecord.getKey().toByteArray());
//        536F6D65206B6579
        var hexEntry = DatatypeConverter.printHexBinary(retryRecord.toByteArray());
//        0A07706F73746D616E120747726F757049641A05747261736820A61228BA8B0232003A08536F6D65206B6579420A536F6D652076616C75654A1C0A0308AC021215726574727965722E696E74657276616C2E5054354D62006A0D4572726F72206D657373616765
        System.out.printf("Stop");
    }
}
