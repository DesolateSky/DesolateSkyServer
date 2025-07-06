package net.desolatesky.instance.region;

import net.minestom.server.coordinate.Point;

public interface Region {

    static Region rectangular(Point min, Point max) {
        return new RectangularRegion(min, max);
    }

    static Region square(Point center, double radius) {
        final Point min = center.sub(radius, radius, radius);
        final Point max = center.add(radius, radius, radius);
        return new RectangularRegion(min, max);
    }

    boolean contains(Point point);

    Point min();

    Point max();

}
