package com.github.cyberpunkperson.retryer.router.domain.archive;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerArchive.ArchiveRecord;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerSource.RetryRecord;


@Mapper(componentModel = "spring")
abstract class ArchiveRecordMapper {

    @Mapping(target = "allFields", ignore = true)
    @Mapping(target = "unknownFields", ignore = true)
    public abstract ArchiveRecord.Builder toArchiveRecordBuilder(RetryRecord retryRecord);


    @ObjectFactory
    protected ArchiveRecord.Builder newBuilder() {
        return ArchiveRecord.newBuilder();
    }
}
