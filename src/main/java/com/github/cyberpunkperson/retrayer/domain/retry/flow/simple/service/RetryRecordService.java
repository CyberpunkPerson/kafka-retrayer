package com.github.cyberpunkperson.retrayer.domain.retry.flow.simple.service;

import com.github.cyberpunkperson.retrayer.domain.retry.RetryRecord;
import com.github.cyberpunkperson.retrayer.domain.retry.flow.simple.repository.RetryRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RetryRecordService {

    private final RetryRecordRepository recordRepository;


    public RetryRecord save(RetryRecord retryRecord) {
        return recordRepository.save(retryRecord);
    }

    public List<RetryRecord> getRetryRecords() {
        return recordRepository.getRetryRecords();
    }
}
