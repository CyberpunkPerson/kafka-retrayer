package com.github.cyberpunkperson.retryer.router.domain.archive;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;
import src.main.java.com.github.cyberpunkperson.retryer.router.Retryer.RetryEntry;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerArchive.ArchiveEntry;


@Mapper(componentModel = "spring")
abstract class ArchiveEntryMapper {

    @Mapping(target = "allFields", ignore = true)
    @Mapping(target = "unknownFields", ignore = true)
    public abstract ArchiveEntry.Builder toArchiveEntryBuilder(RetryEntry retryEntry);


    @ObjectFactory
    protected ArchiveEntry.Builder newBuilder() {
        return ArchiveEntry.newBuilder();
    }
}
