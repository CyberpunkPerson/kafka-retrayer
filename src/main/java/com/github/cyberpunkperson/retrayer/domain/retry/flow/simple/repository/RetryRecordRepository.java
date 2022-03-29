package com.github.cyberpunkperson.retrayer.domain.retry.flow.simple.repository;

import com.github.cyberpunkperson.retrayer.domain.retry.RetryRecord;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface RetryRecordRepository extends CrudRepository<RetryRecord, UUID> {

    @Query("""
            insert into retrayer.retry_record (id, application_name, group_id, topic, record_offset, record_partition, record_timestamp,
                                                record_key, record_payload, delivery_attempt, flow, redelivery_timestamp, error_timestamp,
                                                error_message)
            values (:id, :applicationName, :groupId, :topic, :recordOffset, :recordPartition,
                    :recordTimestamp, :recordKey, :recordPayload, :deliveryAttempt, :flow,
                    :redeliveryTimestamp, :errorTimestamp, :errorMessage)
            returning id, application_name, group_id, topic, record_offset, record_partition, record_timestamp,
                      record_key, record_payload, delivery_attempt, flow, redelivery_timestamp, error_timestamp,
                       error_message
            """)
    RetryRecord save(UUID id, String applicationName, String groupId, String topic, long recordOffset, int recordPartition,
                     Instant recordTimestamp, byte[] recordKey, byte[] recordPayload, int deliveryAttempt, String flow,
                     Instant redeliveryTimestamp, Instant errorTimestamp, String errorMessage);

    @Override
    default RetryRecord save(RetryRecord retryRecord) {
        return this.save(retryRecord.id(), retryRecord.applicationName(), retryRecord.groupId(), retryRecord.topic(), retryRecord.offset(),
                retryRecord.partition(), retryRecord.timestamp(), retryRecord.key(), retryRecord.payload(), retryRecord.deliveryAttempt(),
                retryRecord.flow(), retryRecord.redeliveryTimestamp(), retryRecord.errorTimestamp(), retryRecord.errorMessage());
    }

    @Query("""
            select * 
            from retrayer.retry_record
            where redelivery_timestamp >= now()
            """)
    List<RetryRecord> getRetryRecords();

}
