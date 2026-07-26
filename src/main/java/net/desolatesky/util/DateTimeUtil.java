package net.desolatesky.util;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class DateTimeUtil {

    private DateTimeUtil() {}

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM-dd-yy hh:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM-dd-yy");

    public static String formatInstantToDateTime(Instant instant) {
        return DATE_TIME_FORMATTER.format(instant.atZone(ZoneId.of("EST", ZoneId.SHORT_IDS)));
    }

    public static String formatInstantToDate(Instant instant) {
        return DATE_FORMATTER.format(instant.atZone(ZoneId.of("EST", ZoneId.SHORT_IDS)));
    }

    public static String durationToString(Duration duration) {
        long secondsLeft = duration.getSeconds();
        final StringBuilder builder = new StringBuilder();
        if (secondsLeft > Constants.SECONDS_PER_DAY) {
            builder.append(secondsLeft / Constants.SECONDS_PER_DAY).append(" days");
            secondsLeft %= Constants.SECONDS_PER_DAY;
        }
        if (secondsLeft > Constants.SECONDS_PER_HOUR) {
            appendCommaIfNeeded(builder);
            builder.append(secondsLeft / Constants.SECONDS_PER_HOUR).append(" hours");
            secondsLeft %= Constants.SECONDS_PER_HOUR;
        }
        if (secondsLeft > Constants.SECONDS_PER_MINUTE) {
            appendCommaIfNeeded(builder);
            builder.append(secondsLeft / Constants.SECONDS_PER_MINUTE).append(" minutes");
            secondsLeft %= Constants.SECONDS_PER_MINUTE;
        }
        if (secondsLeft > 0) {
            appendCommaIfNeeded(builder);
            builder.append(secondsLeft).append(" seconds");
        }
        return builder.toString();
    }

    private static void appendCommaIfNeeded(StringBuilder builder) {
        if (!builder.isEmpty()) {
            builder.append(", ");
        }
    }

}
