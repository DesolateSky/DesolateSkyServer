package net.desolatesky.data.type;

import net.desolatesky.data.reader.DataReader;
import net.desolatesky.data.writer.DataWriter;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Point;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface Data<T> {

    Data<Byte> BYTE = new ByteData();
    Data<Boolean> BOOLEAN = new BooleanData();
    Data<Double> DOUBLE = new DoubleData();
    Data<Float> FLOAT = new FloatData();
    Data<Integer> INTEGER = new IntegerData();
    Data<Long> LONG = new LongData();
    Data<Short> SHORT = new ShortData();
    Data<UUID> UUID = new UUIDData();
    Data<String> STRING = new StringData();
    Data<Instant> INSTANT = new InstantData();
    Data<Duration> DURATION = new DurationData();

    Data<Component> COMPONENT = new ComponentData();
    Data<Point> POINT = new PointData();

    void write(DataWriter writer, T value) throws IOException;

    default void writeNullable(DataWriter writer, @Nullable T value) throws IOException {
        if (value == null) {
            writer.write(false);
            return;
        }
        writer.write(true);
        this.write(writer, value);
    }

    default void writeList(DataWriter writer, List<T> values) throws IOException {
        writer.write(values.size());
        for (final T value : values) {
            this.write(writer, value);
        }
    }

    default <K> void writeValueMap(DataWriter writer, Data<K> keyData, Map<K, T> map) throws IOException {
        writer.write(map.size());
        for (final Map.Entry<K, T> entry : map.entrySet()) {
            keyData.write(writer, entry.getKey());
            this.write(writer, entry.getValue());
        }
    }

    default <V> void writeKeyMap(DataWriter writer, Data<V> valueData, Map<T, V> map) throws IOException {
        writer.write(map.size());
        for (final Map.Entry<T, V> entry : map.entrySet()) {
            this.write(writer, entry.getKey());
            valueData.write(writer, entry.getValue());
        }
    }

    T read(DataReader reader) throws IOException;

    default @Nullable T readNullable(DataReader reader) throws IOException {
        final boolean exists = reader.readBoolean();
        if (!exists) {
            return null;
        }
        return this.read(reader);
    }

    default List<T> readList(DataReader reader) throws IOException {
        final List<T> result = new ArrayList<>();
        final int size = reader.readInt();
        for (int i = 0; i < size; i++) {
            result.add(this.read(reader));
        }
        return result;
    }

    default <K> Map<K, T> readValueMap(DataReader reader, Data<K> keyData) throws IOException {
        final Map<K, T> result = new HashMap<>();
        final int size = reader.readInt();
        for (int i = 0; i < size; i++) {
            result.put(keyData.read(reader), this.read(reader));
        }
        return result;
    }

    default <V> Map<T, V> readKeyMap(DataReader reader, Data<V> valueData) throws IOException {
        final Map<T, V> result = new HashMap<>();
        final int size = reader.readInt();
        for (int i = 0; i < size; i++) {
            result.put(this.read(reader), valueData.read(reader));
        }
        return result;
    }
}
