package com.github.cyberpunkperson.retryer.router.domain.retry.flow.simple;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerRouter.RetryerQueueRecord;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerSource.RetryRecord;


@Mapper(componentModel = "spring")
abstract class RetryRecordMapper {

    @Mapping(target = "allFields", ignore = true)
    @Mapping(target = "unknownFields", ignore = true)
    public abstract RetryerQueueRecord.Builder toRetryerQueueRecordBuilder(RetryRecord retryRecord);


    @ObjectFactory
    protected RetryerQueueRecord.Builder newBuilder() {
        return RetryerQueueRecord.newBuilder();
    }
}
