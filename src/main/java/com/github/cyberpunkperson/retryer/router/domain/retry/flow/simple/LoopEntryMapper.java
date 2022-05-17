package com.github.cyberpunkperson.retryer.router.domain.retry.flow.simple;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;
import src.main.java.com.github.cyberpunkperson.retryer.router.Retryer.RetryEntry;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerRouter.LoopEntry;


@Mapper(componentModel = "spring")
abstract class LoopEntryMapper {

    @Mapping(target = "allFields", ignore = true)
    @Mapping(target = "unknownFields", ignore = true)
    public abstract LoopEntry.Builder toLoopEntryBuilder(RetryEntry retryEntry);


    @ObjectFactory
    protected LoopEntry.Builder newBuilder() {
        return LoopEntry.newBuilder();
    }
}
