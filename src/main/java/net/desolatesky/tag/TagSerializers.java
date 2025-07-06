package net.desolatesky.tag;

import net.desolatesky.tag.serializer.TagMapSerializer;
import net.minestom.server.tag.TagSerializer;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class TagSerializers {

    private TagSerializers() {
        throw new UnsupportedOperationException();
    }

    public static <K, V> TagMapSerializer<K, V> map(Supplier<Map<K, V>> mapSupplier, TagSerializer<K> keySerializer, TagSerializer<V> valueSerializer) {
        return new TagMapSerializer<>(mapSupplier, keySerializer, valueSerializer);
    }

    public static <K, V> TagMapSerializer<K, V> hashMap(TagSerializer<K> keySerializer, TagSerializer<V> valueSerializer) {
        return new TagMapSerializer<>(HashMap::new, keySerializer, valueSerializer);
    }

    public static <K, V> TagMapSerializer<K, V> linkedHashMap(TagSerializer<K> keySerializer, TagSerializer<V> valueSerializer) {
        return new TagMapSerializer<>(LinkedHashMap::new, keySerializer, valueSerializer);
    }

}
