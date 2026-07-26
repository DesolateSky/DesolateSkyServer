package net.desolatesky.data.type;

import net.desolatesky.data.reader.DataReader;
import net.desolatesky.data.writer.DataWriter;
import net.desolatesky.world.region.PointType;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;

import java.io.IOException;

public final class PointData implements Data<Point> {

    @Override
    public void write(DataWriter writer, Point value) throws IOException {
        switch (value) {
            case Vec v -> {
                PointType.DATA.write(writer, PointType.VEC);
                Data.DOUBLE.write(writer, v.x());
                Data.DOUBLE.write(writer, v.y());
                Data.DOUBLE.write(writer, v.z());
            }
            case BlockVec v -> {
                PointType.DATA.write(writer, PointType.BLOCK_VEC);
                Data.INTEGER.write(writer, v.blockX());
                Data.INTEGER.write(writer, v.blockY());
                Data.INTEGER.write(writer, v.blockZ());
            }
            case Pos p -> {
                PointType.DATA.write(writer, PointType.POS);
                Data.DOUBLE.write(writer, p.x());
                Data.DOUBLE.write(writer, p.y());
                Data.DOUBLE.write(writer, p.z());
                Data.FLOAT.write(writer, p.yaw());
                Data.FLOAT.write(writer, p.pitch());
            }
        }
    }

    @Override
    public Point read(DataReader reader) throws IOException {
        final PointType type = PointType.DATA.read(reader);
        return switch (type) {
            case VEC -> {
                final double x = Data.DOUBLE.read(reader);
                final double y = Data.DOUBLE.read(reader);
                final double z = Data.DOUBLE.read(reader);
                yield new Vec(x, y, z);
            }
            case BLOCK_VEC -> {
                final int x = Data.INTEGER.read(reader);
                final int y = Data.INTEGER.read(reader);
                final int z = Data.INTEGER.read(reader);
                yield new BlockVec(x, y, z);
            }
            case POS -> {
                final double x = Data.DOUBLE.read(reader);
                final double y = Data.DOUBLE.read(reader);
                final double z = Data.DOUBLE.read(reader);
                final float yaw = Data.FLOAT.read(reader);
                final float pitch = Data.FLOAT.read(reader);
                yield new Pos(x, y, z, yaw, pitch);
            }
        };
    }
}
