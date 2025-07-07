package net.desolatesky.util;

import com.google.common.base.Preconditions;

import java.util.Arrays;
import java.util.function.Predicate;

public final class ArrayUtil {

    private ArrayUtil() {
        throw new UnsupportedOperationException();
    }

    public static <T> void shiftToTopLeftCorner(T[][] array, T[][] copyArray, Predicate<T> emptyPredicate, T emptyValue) {
        Preconditions.checkArgument(array.length == copyArray.length, "Array lengths must match");
        Preconditions.checkArgument(array[0].length == copyArray[0].length, "Array column lengths must match");
        final int rows = array.length;
        final int columns = array[0].length;

        boolean foundNonEmptyRow = false;
        boolean foundNonEmptyColumn = false;
        int row = 0;
        int col = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                final boolean empty = emptyPredicate.test(array[i][j]);
                if (!foundNonEmptyRow && !empty) {
                    row = i;
                    foundNonEmptyRow = true;
                }
                if (!empty) {
                    if (foundNonEmptyColumn) {
                        col = Math.min(col, j);
                    } else {
                        col = j;
                        foundNonEmptyColumn = true;
                    }
                }
            }
        }
        for (int i = 0; i < rows - row; i++) {
            Arrays.fill(copyArray[i], emptyValue);
        }
        for (int i = row; i < rows; i++) {
            for (int j = col; j < columns; j++) {
                if (emptyPredicate.test(array[i][j])) {
                    copyArray[i - row][j - col] = emptyValue;
                } else {
                    copyArray[i - row][j - col] = array[i][j];
                }
            }
        }
    }

}
