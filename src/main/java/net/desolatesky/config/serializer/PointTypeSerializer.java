
package net.desolatesky.config.serializer;

import net.desolatesky.util.NumberUtil;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class PointTypeSerializer implements TypeSerializer<Point> {

    @Override
    public Point deserialize(Type type, ConfigurationNode node) throws SerializationException {
        final List<Double> coords = node.getList(Double.class, new ArrayList<>());
        if (coords.size() < 3) {
            return null;
        }
        final double x = coords.get(0);
        final double y = coords.get(1);
        final double z = coords.get(2);
        if (coords.size() == 3) {
            if (NumberUtil.isInteger(x) && NumberUtil.isInteger(y) && NumberUtil.isInteger(z)) {
                return new BlockVec(x, y, z);
            }
            return new Vec(x, y, z);
        }
        if (coords.size() == 5) {
            final float yaw = (float) (double) coords.get(3);
            final float pitch = (float) (double) coords.get(4);
            return new Pos(x, y, z, yaw, pitch);
        }
        throw new SerializationException(coords.stream().map(String::valueOf).collect(Collectors.joining(", ")) + " is not a valid point");
    }

    @Override
    public void serialize(Type type, @Nullable Point obj, ConfigurationNode node) throws SerializationException {
    }
}
