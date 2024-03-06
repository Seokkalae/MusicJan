package org.seokkalae.musicjan.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Converter {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static String toFormattedDuration(Long duration) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(duration), ZoneId.systemDefault());
        return dateTime.format(formatter);
    }
}
