package net.desolatesky.config.serializer;

import net.kyori.adventure.key.Key;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public final class KeyTypeSerializer implements TypeSerializer<Key> {

    @Override
    public Key deserialize(Type type, ConfigurationNode node) throws SerializationException {
        return Key.key(node.getString(""));
    }

    @Override
    public void serialize(Type type, @Nullable Key obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.set((String)null);
            return;
        }
        node.set(obj.asString());
    }
}
