package net.desolatesky.util;

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

}
