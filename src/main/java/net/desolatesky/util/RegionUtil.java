package net.desolatesky.util;

import net.desolatesky.world.region.SquareRegion;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;

public final class RegionUtil {

    private RegionUtil() {
    }

    public static Point getClosestBorderPointTo(SquareRegion region, Point point) {
        final double xDistFromMin = point.x() - region.min().x();
        final double xDistFromMax = region.max().x() - point.x();
        final double zDistFromMin = point.z() - region.min().z();
        final double zDistFromMax = region.max().z() - point.z();

        final double closest = Math.min(
                Math.min(xDistFromMin, xDistFromMax),
                Math.min(zDistFromMin, zDistFromMax)
        );

        double closestX = point.x();
        double closestZ = point.z();

        if (closest == xDistFromMin) {
            closestX = region.min().x();
        } else if (closest == xDistFromMax) {
            closestX = region.min().x();
        } else if (closest == zDistFromMin) {
            closestZ = region.min().z();
        } else {
            closestZ = region.max().z();
        }
        return new Vec(closestX, point.y(), closestZ);
    }

}
