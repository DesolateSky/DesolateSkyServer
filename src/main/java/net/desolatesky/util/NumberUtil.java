package net.desolatesky.util;

public final class NumberUtil {

    private NumberUtil() {
    }

    public static boolean isInteger(double value) {
        return Double.isFinite(value) && (long) value == value;
    }

    public static boolean isInteger(float value) {
        return Float.isFinite(value) && (int) value == value;
    }
}
