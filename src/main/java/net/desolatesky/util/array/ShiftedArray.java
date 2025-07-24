package net.desolatesky.util.array;

import com.google.common.base.Preconditions;
import net.desolatesky.util.collection.Pair;

import java.util.Arrays;
import java.util.function.Predicate;

public record ShiftedArray<T>(T[][] array, int rowShift, int colShift, int actualRows, int actualColumns) {

    public static <T> ShiftedArray<T> shiftToTopLeftCorner(T[][] array, T[][] copyArray, Predicate<T> emptyPredicate, T emptyValue) {
        Preconditions.checkArgument(array.length <= copyArray.length, "Array row length must be less than or equal to copy array row length");
        Preconditions.checkArgument(array[0].length <= copyArray[0].length, "Array column length must be less than or equal to copy array column length");
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
            System.arraycopy(array[i], col, copyArray[i - row], 0, columns - col);
        }
        final Pair<Integer, Integer> heightAndWidth = getHeightAndWidth(copyArray, emptyPredicate);
        return new ShiftedArray<>(copyArray, row, col, heightAndWidth.first(), heightAndWidth.second());
    }

    public static <T> Pair<Integer, Integer> getHeightAndWidth(T[][] array, Predicate<T> emptyPredicate) {
        Preconditions.checkArgument(array.length > 0, "Array must have at least one row");
        Preconditions.checkArgument(array[0].length > 0, "Array must have at least one column");
        int height = 0;
        int width = 0;
        for (int i = array.length - 1; i >= 0; i--) {
            boolean emptyRow = true;
            for (int j = 0; j < array[i].length; j++) {
                if (!emptyPredicate.test(array[i][j])) {
                    emptyRow = false;
                    width = Math.max(width, j + 1);
                }
            }
            if (!emptyRow) {
                height = Math.max(height, i + 1);
            }
        }
        return Pair.of(height, width);
    }

}
