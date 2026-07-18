package net.desolatesky.world.region.data;

import net.desolatesky.data.definition.DataDefinition;
import net.desolatesky.data.reader.DataReader;
import net.desolatesky.data.type.Data;
import net.desolatesky.data.writer.DataWriter;
import net.desolatesky.world.region.RectangularRegion;
import net.desolatesky.world.region.Region;
import net.desolatesky.world.region.RegionType;
import net.desolatesky.world.region.SquareRegion;
import net.minestom.server.coordinate.Point;

import java.io.IOException;

public final class RegionDataDefinitionV1 extends DataDefinition<Region> {

    public RegionDataDefinitionV1() {
        super(1);
    }

    @Override
    public void write(DataWriter writer, Region value) throws IOException {
        switch (value) {
            case SquareRegion region -> {
                RegionType.DATA.write(writer, RegionType.SQUARE);
                Data.POINT.write(writer, region.center());
                Data.DOUBLE.write(writer, region.radius());
                Data.INTEGER.write(writer, region.minY());
                Data.INTEGER.write(writer, region.maxY());
            }
            case RectangularRegion region -> {
                RegionType.DATA.write(writer, RegionType.RECTANGULAR);
                Data.POINT.write(writer, region.min());
                Data.POINT.write(writer, value.max());
            }
            default -> {
                throw new IllegalArgumentException(value.getClass().getName());
            }
        }
    }

    @Override
    public Region read(DataReader reader) throws IOException {
        final RegionType type = RegionType.DATA.read(reader);
        return switch (type) {
            case SQUARE -> {
                final Point center = Data.POINT.read(reader);
                final double radius = Data.DOUBLE.read(reader);
                final int minY = Data.INTEGER.read(reader);
                final int maxY = Data.INTEGER.read(reader);
                yield new SquareRegion(center, radius, minY, maxY);
            }
            case RECTANGULAR -> {
                final Point min = Data.POINT.read(reader);
                final Point max = Data.POINT.read(reader);
                yield new RectangularRegion(min, max);
            }
        };
    }
}
