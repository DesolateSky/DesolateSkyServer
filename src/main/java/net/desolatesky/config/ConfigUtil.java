package net.desolatesky.config;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

public final class ConfigUtil {

    private ConfigUtil() {
        throw new UnsupportedOperationException();
    }

    public static ConfigurationNode nonVirtualNode(ConfigurationNode source, Object... path) throws SerializationException {
        if (!source.hasChild(path)) {
            throw new SerializationException("Required field " + Arrays.toString(path) + " was not present in node");
        }
        return source.node(path);
    }

    public static <T> @Nullable T getNullable(ConfigurationNode source, Class<T> type, Object... path) throws SerializationException {
        return source.node(path).get(type);
    }

    public static <T> @Nullable T getNullable(ConfigurationNode source, Class<T> type) throws SerializationException {
        return source.get(type);
    }

    public static <T> T getNonNull(ConfigurationNode source, Class<T> type, Object... path) throws SerializationException {
        final ConfigurationNode node = nonVirtualNode(source, path);
        return Objects.requireNonNull(node.get(type));
    }

    public static <T> T getNonNullElse(ConfigurationNode source, Class<T> type, Supplier<@Nullable T> defaultValue, Object... path) throws SerializationException {
        final ConfigurationNode node = source.node(path);
        if (node.virtual()) {
            return defaultValue.get();
        }
        return Objects.requireNonNullElseGet(node.get(type), defaultValue);
    }

    public static <T> T getNonNull(ConfigurationNode source, Class<T> type) throws SerializationException {
        if (source.virtual()) {
            throw new SerializationException("Required field " + source.path() + " was not present in node");
        }
        return Objects.requireNonNull(source.get(type));
    }

}
