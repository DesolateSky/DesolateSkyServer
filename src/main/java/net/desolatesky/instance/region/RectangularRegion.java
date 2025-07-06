package net.desolatesky.instance.region;

import net.minestom.server.coordinate.Point;

public class RectangularRegion implements Region{

    private final Point min;
    private final Point max;

    public RectangularRegion(Point min, Point max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean contains(Point point) {
        final double x = point.x();
        final double y = point.y();
        final double z = point.z();
        return x >= this.min.x() && x <= this.max.x() &&
               y >= this.min.y() && y <= this.max.y() &&
               z >= this.min.z() && z <= this.max.z();
    }

    @Override
    public Point min() {
        return this.min;
    }

    @Override
    public Point max() {
        return this.max;
    }

}
