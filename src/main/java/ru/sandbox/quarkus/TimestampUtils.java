package ru.sandbox.quarkus;

import com.google.protobuf.Timestamp;

import java.time.Instant;

public final class TimestampUtils {

    private TimestampUtils() {
    }

    public static Timestamp now() {
        return toTimestamp(Instant.now().toEpochMilli());
    }

    public static Timestamp toTimestamp(long milli) {
        return Timestamp.newBuilder().setSeconds(milli).build();
    }
}
