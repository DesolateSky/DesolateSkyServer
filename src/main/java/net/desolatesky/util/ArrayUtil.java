package net.desolatesky.util;

public final class ArrayUtil {

    private ArrayUtil() {
        throw new UnsupportedOperationException();
    }

    public static <T> void toTwoDimensionalArray(T[] input, T[][] output) {
        final int length = (int) Math.sqrt(input.length);
        for (int i = 0; i < length; i++) {
            System.arraycopy(input, i * length, output[i], 0, length);
        }
    }

}
