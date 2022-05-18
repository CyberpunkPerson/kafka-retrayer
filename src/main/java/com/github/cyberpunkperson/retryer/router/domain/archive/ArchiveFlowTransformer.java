package com.github.cyberpunkperson.retryer.router.domain.archive;

import lombok.RequiredArgsConstructor;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.stereotype.Component;
import src.main.java.com.github.cyberpunkperson.retryer.router.Retryer.RetryEntry;
import src.main.java.com.github.cyberpunkperson.retryer.router.RetryerArchive.ArchiveEntry;

@Component
@RequiredArgsConstructor
class ArchiveFlowTransformer implements GenericTransformer<RetryEntry, ArchiveEntry> {

    private final ArchiveEntryMapper archiveEntryMapper;

    @Override
    public ArchiveEntry transform(RetryEntry retryEntry) {
        return archiveEntryMapper.toArchiveEntryBuilder(retryEntry).build();
    }
}
