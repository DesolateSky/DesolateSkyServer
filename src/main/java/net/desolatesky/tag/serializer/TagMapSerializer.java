package net.desolatesky.tag.serializer;

import net.desolatesky.util.Tags;
import net.minestom.server.tag.Tag;
import net.minestom.server.tag.TagReadable;
import net.minestom.server.tag.TagSerializer;
import net.minestom.server.tag.TagWritable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Supplier;

public class TagMapSerializer<K, V> implements TagSerializer<Map<K, V>> {

    private static final Tag<Integer> SIZE_TAG = Tags.Integer("size");

    private final Supplier<Map<K, V>> mapSupplier;
    private final TagSerializer<K> keySerializer;
    private final TagSerializer<V> valueSerializer;

    public TagMapSerializer(Supplier<Map<K, V>> mapSupplier, TagSerializer<K> keySerializer, TagSerializer<V> valueSerializer) {
        this.mapSupplier = mapSupplier;
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
    }

    @Override
    public Map<K, V> read(@NotNull TagReadable reader) {
        final int size = reader.getTag(SIZE_TAG);
        final Map<K, V> map = this.mapSupplier.get();
        if (size < 0) {
            return map;
        }
        for (int i = 0; i < size; i++) {
            final K key = this.keySerializer.read(reader);
            final V value = this.valueSerializer.read(reader);
            map.put(key, value);
        }
        return map;
    }

    @Override
    public void write(@NotNull TagWritable writer, @NotNull Map<K, V> map) {
        writer.setTag(SIZE_TAG, map.size());
        for (final Map.Entry<K, V> entry : map.entrySet()) {
            final K key = entry.getKey();
            final V value = entry.getValue();
            this.keySerializer.write(writer, key);
            this.valueSerializer.write(writer, value);
        }
    }

}
