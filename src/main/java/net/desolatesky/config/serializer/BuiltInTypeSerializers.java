package net.desolatesky.config.serializer;

import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.registry.TagKey;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

public final class BuiltInTypeSerializers {

    private BuiltInTypeSerializers() {
    }

    public static void registerToLoader(TypeSerializerCollection.Builder builder) {
        builder.register(Key.class, new KeyTypeSerializer());
        builder.register(Point.class, new PointTypeSerializer());
    }

}
