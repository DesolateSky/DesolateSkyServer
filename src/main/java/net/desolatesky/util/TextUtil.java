package net.desolatesky.util;

import java.time.Duration;

public final class TextUtil {

    private TextUtil() {
        throw new UnsupportedOperationException();
    }

    public static String capitalize(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    public static String capitalize(String text, String delimiter, String delimiterReplacement) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        final String[] parts = text.split(delimiter);
        final StringBuilder capitalizedText = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                capitalizedText.append(capitalize(part)).append(delimiterReplacement);
            }
        }
        if (!capitalizedText.isEmpty()) {
            capitalizedText.setLength(capitalizedText.length() - delimiterReplacement.length());
        }
        return capitalizedText.toString();
    }

    public static String capitalize(String text, String delimiter) {
        return capitalize(text, delimiter, " ");
    }

    public static String formatDuration(Duration duration) {
        StringBuilder builder = new StringBuilder();
        if (duration.toDays() > 0) {
            builder = builder.append(duration.toDays()).append(" days");
        }
        if (duration.toHours() % 24 > 0) {
            addSeparator(builder, ", ").append(duration.toHours() % 24).append(" hours");
        }
        if (duration.toMinutes() % 60 > 0) {
            addSeparator(builder, ", ").append(duration.toMinutes() % 60).append(" minutes");
        }
        if (duration.toSeconds() % 60  > 0) {
            addSeparator(builder, ", ").append(duration.toSeconds() % 60).append(" seconds");
        }
        if (builder.isEmpty()) {
            return "0 seconds";
        }
        return builder.toString();
    }

    private static StringBuilder addSeparator(StringBuilder builder, String separator) {
        if (!builder.isEmpty()) {
            return builder.append(separator);
        }
        return builder;
    }

}
