package com.github.cyberpunkperson.retryer.router.domain.router.flow.simple;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerRouter.RouterQueueRecord;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerSource.RetryRecord;

import static com.github.cyberpunkperson.retryer.router.support.util.ProtobufUtil.toTimestamp;
import static com.github.cyberpunkperson.retryer.router.support.util.ProtobufUtil.uuidToByteString;
import static java.time.Instant.now;
import static java.util.UUID.randomUUID;


@Mapper
abstract class RetryRecordMapper {

    @Mapping(target = "allFields", ignore = true)
    @Mapping(target = "unknownFields", ignore = true)
    @Mapping(target = "redeliveryTimestamp", ignore = true)
    public abstract RouterQueueRecord.Builder newRetryerQueueRecordBuilder(RetryRecord retryRecord);


    @ObjectFactory
    protected RouterQueueRecord.Builder newBuilder() {
        return RouterQueueRecord.newBuilder()
                .setId(uuidToByteString(randomUUID()))
                .setCreatedAt(toTimestamp(now()));
    }
}
