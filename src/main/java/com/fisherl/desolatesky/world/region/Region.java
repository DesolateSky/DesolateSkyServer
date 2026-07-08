package com.fisherl.desolatesky.world.region;

import net.minestom.server.coordinate.Point;

public interface Region {

    static Region rectangular(Point min, Point max) {
        return new RectangularRegion(min, max);
    }

    static SquareRegion square(Point center, double radius, int minY, int maxY) {
        return new SquareRegion(center, radius, minY, maxY);
    }

    boolean contains(Point point);

    Point min();

    Point max();

}
