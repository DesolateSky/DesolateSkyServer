package com.fisherl.desolatesky.world.region;

import net.minestom.server.coordinate.Point;

public final class SquareRegion extends RectangularRegion {

    private final Point center;
    private final double radius;

    public SquareRegion(Point center, double radius, int minY, int maxY) {
        this.center = center;
        this.radius = radius;
        final Point min = center.sub(radius, 0, radius).withY(minY);
        final Point max = center.add(radius, 0, radius).withY(maxY);
        super(min, max);
    }

    public Point center() {
        return this.center;
    }

    public double radius() {
        return this.radius;
    }
}
