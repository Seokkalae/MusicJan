package org.seokkalae.musicjan.utils;

import java.time.Duration;

public class Converter {

    public static String toFormattedDuration(Long duration) {
        Duration trueDuration = Duration.ofMillis(duration);
        long hours = trueDuration.toHours();
        long minutes = trueDuration.toMinutesPart();
        long seconds = trueDuration.toSecondsPart();
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
