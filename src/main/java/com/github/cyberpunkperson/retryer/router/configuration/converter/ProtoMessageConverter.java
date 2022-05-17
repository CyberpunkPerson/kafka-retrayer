package com.github.cyberpunkperson.retryer.router.configuration.converter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.converter.ConversionException;
import org.springframework.kafka.support.converter.MessagingMessageConverter;

import java.lang.reflect.Type;

@RequiredArgsConstructor
public class ProtoMessageConverter<T extends MessageLite> extends MessagingMessageConverter {

    private final Parser<T> parser;

    @Override
    protected Object extractAndConvertValue(ConsumerRecord<?, ?> record, Type type) {
        try {
            return parser.parseFrom((byte[]) record.value());
        } catch (InvalidProtocolBufferException e) {
            throw new ConversionException(e.getMessage(), e);
        }
    }
}
