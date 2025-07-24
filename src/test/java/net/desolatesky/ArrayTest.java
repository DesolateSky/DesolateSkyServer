package net.desolatesky;

import net.desolatesky.util.array.ShiftedArray;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class ArrayTest {

    @Test
    public void testNoShift() {
        final String[][] given = new String[][]{
                {"1", "2", "3"},
                {"4", "5", "6"},
                {"7", "8", "9"}
        };
        final String[][] expected = new String[][]{
                {"1", "2", "3"},
                {"4", "5", "6"},
                {"7", "8", "9"}
        };
        final String[][] copy = new String[3][3];
        ShiftedArray.shiftToTopLeftCorner(given, copy, Objects::isNull, null);
        assertArrayEquals(expected, copy);
    }

    @Test
    public void testShiftUp() {
        final String[][] given = new String[][]{
                {null, null, null},
                {"4", "5", "6"},
                {"7", "8", "9"}
        };
        final String[][] expected = new String[][]{
                {"4", "5", "6"},
                {"7", "8", "9"},
                {null, null, null}
        };
        final String[][] copy = new String[3][3];
        ShiftedArray.shiftToTopLeftCorner(given, copy, Objects::isNull, null);
        assertArrayEquals(expected, copy);
    }

    @Test
    public void testShiftLeft() {
        final String[][] given = new String[][]{
                {null, null, null},
                {null, "5", "6"},
                {null, "8", "9"}
        };
        final String[][] expected = new String[][]{
                {"5", "6", null},
                {"8", "9", null},
                {null, null, null}
        };
        final String[][] copy = new String[3][3];
        ShiftedArray.shiftToTopLeftCorner(given, copy, Objects::isNull, null);
        assertArrayEquals(expected, copy);
    }

    @Test
    public void testShiftWithGap() {
        final String[][] given = new String[][]{
                {null, null, null},
                {"4", null, "6"},
                {"7", null, "9"}
        };
        final String[][] expected = new String[][]{
                {"4", null, "6"},
                {"7", null, "9"},
                {null, null, null}
        };
        final String[][] copy = new String[3][3];
        ShiftedArray.shiftToTopLeftCorner(given, copy, Objects::isNull, null);
        assertArrayEquals(expected, copy);
    }


}
