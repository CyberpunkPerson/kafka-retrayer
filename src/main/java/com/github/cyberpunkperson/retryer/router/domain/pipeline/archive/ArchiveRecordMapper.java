package com.github.cyberpunkperson.retryer.router.domain.pipeline.archive;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerArchive.ArchiveRecord;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerEntry.RetryRecord;

import static com.github.cyberpunkperson.retryer.router.support.util.ProtobufUtil.toTimestamp;
import static com.github.cyberpunkperson.retryer.router.support.util.ProtobufUtil.uuidToByteString;
import static java.time.Instant.now;
import static java.util.UUID.randomUUID;


@Mapper
abstract class ArchiveRecordMapper {

    @Mapping(target = "allFields", ignore = true)
    @Mapping(target = "unknownFields", ignore = true)
    public abstract ArchiveRecord buildArchiveRecord(RetryRecord retryRecord);


    @ObjectFactory
    protected ArchiveRecord.Builder newBuilder() {
        return ArchiveRecord.newBuilder()
                .setId(uuidToByteString(randomUUID()))
                .setCreatedAt(toTimestamp(now()));
    }
}
