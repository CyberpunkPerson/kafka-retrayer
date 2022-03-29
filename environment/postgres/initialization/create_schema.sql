create schema retrayer
    authorization postgres;

create table retrayer.retry_record
(
    id                   uuid   not null,
    application_name     text   not null,
    group_id             text   not null,
    topic                text   not null,
    record_offset        bigint not null,
    record_partition     int    not null,
    record_timestamp     timestamp without time zone,
    record_key           bytea  not null,
    record_payload       bytea  not null,
    delivery_attempt     int    not null,
    flow                 text   not null,
    redelivery_timestamp timestamp without time zone,
    error_timestamp      timestamp without time zone,
    error_message        text   not null,

    constraint retry_record_pk primary key (id)
);

create index retry_record_redelivery_timestamp
    on retrayer.retry_record (redelivery_timestamp desc);