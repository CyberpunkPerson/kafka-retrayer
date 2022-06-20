package com.github.cyberpunkperson.retryer.router.support.util;

import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import lombok.experimental.UtilityClass;

import java.time.*;
import java.util.UUID;

import static ru.progrm_jarvis.javacommons.util.UuidUtil.uuidToBytes;

@UtilityClass
public class ProtobufUtil {

    public static ByteString uuidToByteString(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        return ByteString.copyFrom(uuidToBytes(uuid));
    }

    public static Timestamp toTimestamp(Instant instant) {
        return buildTimestamp(instant.getEpochSecond(), instant.getNano());
    }

    public static Timestamp toTimestamp(LocalDateTime localDateTime, ZoneId zoneId) {
        if (localDateTime == null) {
            return null;
        }
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        return toTimestamp(zonedDateTime);
    }

    public static Timestamp toTimestamp(OffsetDateTime dateTime) {
        return buildTimestamp(dateTime.toEpochSecond(), dateTime.getNano());
    }

    public static Timestamp toTimestamp(ZonedDateTime dateTime) {
        return buildTimestamp(dateTime.toEpochSecond(), dateTime.getNano());
    }

    private static Timestamp buildTimestamp(long seconds, int nanos) {
        return Timestamp.newBuilder()
                .setSeconds(seconds)
                .setNanos(nanos)
                .build();
    }
}
