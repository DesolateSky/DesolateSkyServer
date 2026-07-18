package net.desolatesky.world.region;

import net.desolatesky.data.definition.DataTranslator;
import net.desolatesky.world.region.data.RegionDataDefinitionV1;
import net.minestom.server.coordinate.Point;

import java.util.List;

public interface Region {

    DataTranslator<Region> DATA_TRANSLATOR = new DataTranslator<>(List.of(
            new RegionDataDefinitionV1()
    ));

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
