package com.github.cyberpunkperson.retryer.router.domain.archive;

import lombok.RequiredArgsConstructor;
import org.springframework.integration.core.GenericSelector;
import org.springframework.stereotype.Component;
import src.main.java.com.github.cyberpunkperson.retryer.router.Retryer.RetryEntry;

@RequiredArgsConstructor
@Component("archiveFlowFilter")
class ArchiveFlowFilter implements GenericSelector<RetryEntry> {

    @Override
    public boolean accept(RetryEntry context) {
        return false; //todo impl
    }
}
