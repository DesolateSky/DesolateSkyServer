package net.desolatesky.util;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import net.minestom.server.tag.TagReadable;
import net.minestom.server.tag.TagSerializer;
import net.minestom.server.tag.TagWritable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NotNullByDefault;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

@NotNullByDefault
public final class Tags {

    private Tags() {
        throw new UnsupportedOperationException();
    }

    public static Tag<Key> Key(String name) {
        return Tag.String(name).map(Key::key, Key::asString);
    }

    public static Tag<Duration> Duration(String name) {
        return Tag.Long(name).map(Duration::ofMillis, Duration::toMillis);
    }

    public static Tag<Instant> Instant(String name) {
        return Tag.Long(name).map(Instant::ofEpochMilli, Instant::toEpochMilli);
    }

    public static Tag<Key> NamespaceKey(String name) {
        return Tag.String(name).map(Namespace::key, Key::asString);
    }

    public static Tag<Byte> Byte(String key) {
        return Tag.Byte(key);
    }

    public static Tag<Boolean> Boolean(String key) {
        return Tag.Boolean(key);
    }

    public static Tag<Short> Short(String key) {
        return Tag.Short(key);
    }

    public static Tag<Integer> Integer(String key) {
        return Tag.Integer(key);
    }

    public static Tag<Long> Long(String key) {
        return Tag.Long(key);
    }

    public static Tag<Float> Float(String key) {
        return Tag.Float(key);
    }

    public static Tag<Double> Double(String key) {
        return Tag.Double(key);
    }

    public static Tag<String> String(String key) {
        return Tag.String(key);
    }

    public static Tag<UUID> UUID(String key) {
        return Tag.UUID(key);
    }

    public static Tag<ItemStack> ItemStack(String key) {
        return Tag.ItemStack(key);
    }

    public static Tag<Component> Component(String key) {
        return Tag.Component(key);
    }

    public static Tag<BinaryTag> NBT(String key) {
        return Tag.NBT(key);
    }

    public static <F, S> Tag<Pair<F, S>> Pair(String key, Tag<F> firstTag, Tag<S> secondTag) {
        return Tag.Structure(key, new TagSerializer<Pair<F, S>>() {
            @Override
            public Pair<F, S> read(TagReadable reader) {
                final F first = reader.getTag(firstTag);
                final S second = reader.getTag(secondTag);
                return new Pair<>(first, second);
            }

            @Override
            public void write(TagWritable writer, Pair<F, S> value) {
                writer.setTag(firstTag, value.first());
                writer.setTag(secondTag, value.second());
            }
        });
    }
    
    public static <K, V> Tag<Map<K, V>> Map(String key, Tag<K> keyTag, Tag<V> valueTag, Supplier<Map<K, V>> mapFactory) {
        return Tag.Structure(key, new TagSerializer<>() {

            private final Tag<List<K>> keyListTag = keyTag.list();
            private final Tag<List<V>> valueListTag = valueTag.list();

            @Override
            public Map<K, V> read(TagReadable reader) {
                final List<K> keys = reader.getTag(this.keyListTag);
                final List<V> values = reader.getTag(this.valueListTag);
                if (keys == null || values == null || keys.size() != values.size()) {
                    return Collections.emptyMap();
                }
                final Map<K, V> map = new HashMap<>();
                final Iterator<K> keyIterator = keys.iterator();
                final Iterator<V> valueIterator = values.iterator();
                while (keyIterator.hasNext() && valueIterator.hasNext()) {
                    final K key = keyIterator.next();
                    final V value = valueIterator.next();
                    map.put(key, value);
                }
                return map;
            }

            @Override
            public void write(TagWritable writer, Map<K, V> value) {
                final List<K> keys = new ArrayList<>();
                final List<V> values = new ArrayList<>();
                for (final Map.Entry<K, V> entry : value.entrySet()) {
                    keys.add(entry.getKey());
                    values.add(entry.getValue());
                }
                writer.setTag(this.keyListTag, keys);
                writer.setTag(this.valueListTag, values);
            }
        });
    }

    public static <E extends Enum<E>> Tag<E> Enum(String key, Class<E> enumClass) {
        return Tag.String(key).map(name -> Enum.valueOf(enumClass, name), E::name);
    }

    public static <T> Tag<T> Structure(String key, TagSerializer<T> serializer) {
        return Tag.Structure(key, serializer);
    }

    public static <T> Tag<T> View(TagSerializer<T> serializer) {
        return Tag.View(serializer);
    }

    @ApiStatus.Experimental
    public static <T extends Record> Tag<T> Structure(String key, Class<T> type) {
        return Tag.Structure(key, type);
    }

    @ApiStatus.Experimental
    public static <T extends Record> Tag<T> View(Class<T> type) {
        return Tag.View(type);
    }

    public static <T> Tag<T> Transient(String key) {
        return Tag.Transient(key);
    }
}
