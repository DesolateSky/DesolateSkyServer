package net.desolatesky.util;

import net.minestom.server.coordinate.Point;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.List;

public final class DirectionUtil {

    public static final @Unmodifiable List<Direction> ALL_DIRECTIONS = Arrays.stream(Direction.values()).toList();

    private DirectionUtil() {
        throw new UnsupportedOperationException();
    }

    public static Direction getDirectionBetween(Point from, Point to) {
        final double dx = to.x() - from.x();
        final double dy = to.y() - from.y();
        final double dz = to.z() - from.z();

        if (Math.abs(dx) > Math.abs(dy) && Math.abs(dx) > Math.abs(dz)) {
            return dx > 0 ? Direction.EAST : Direction.WEST;
        } else if (Math.abs(dy) > Math.abs(dx) && Math.abs(dy) > Math.abs(dz)) {
            return dy > 0 ? Direction.UP : Direction.DOWN;
        } else {
            return dz > 0 ? Direction.SOUTH : Direction.NORTH;
        }
    }

}
