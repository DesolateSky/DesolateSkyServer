package net.desolatesky.data.definition;

import net.desolatesky.data.reader.DataReader;
import net.desolatesky.data.type.Data;
import net.desolatesky.data.writer.DataWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public final class DataTranslator<T> implements Data<T> {

    private final List<DataDefinition<T>> definitions;

    public DataTranslator(List<DataDefinition<T>> definitions) {
        this.definitions = new ArrayList<>(definitions);
        this.definitions.sort(Comparator.comparingInt(DataDefinition::version));
    }

    public static <T> DataTranslator<T> create(DataDefinition<T> definition) {
        return new DataTranslator<>(List.of(definition));
    }

    @SafeVarargs
    public static <T> DataTranslator<T> create(DataDefinition<T>... definitions) {
        return new DataTranslator<>(Arrays.asList(definitions));
    }

    @Override
    public void write(DataWriter writer, T value) throws IOException {
        this.checkDefinitions();
        final DataDefinition<T> definition = this.definitions.getLast();
        writer.write(definition.version());
        definition.write(writer, value);
    }

    @Override
    public void writeList(DataWriter writer, List<T> values) throws IOException {
        this.checkDefinitions();
        Data.super.writeList(writer, values);
    }

    @Override
    public <K> void writeValueMap(DataWriter writer, Data<K> keyData, Map<K, T> map) throws IOException {
        this.checkDefinitions();
        Data.super.writeValueMap(writer, keyData, map);
    }

    @Override
    public <V> void writeKeyMap(DataWriter writer, Data<V> valueData, Map<T, V> map) throws IOException {
        this.checkDefinitions();
        Data.super.writeKeyMap(writer, valueData, map);
    }

    @Override
    public List<T> readList(DataReader reader) throws IOException {
        this.checkDefinitions();
        return Data.super.readList(reader);
    }

    @Override
    public <K> Map<K, T> readValueMap(DataReader reader, Data<K> keyData) throws IOException {
        this.checkDefinitions();
        return Data.super.readValueMap(reader, keyData);
    }

    @Override
    public <V> Map<T, V> readKeyMap(DataReader reader, Data<V> valueData) throws IOException {
        this.checkDefinitions();
        return Data.super.readKeyMap(reader, valueData);
    }

    @Override
    public T read(DataReader reader) throws IOException {
        this.checkDefinitions();
        final int version = reader.readInt();
        DataDefinition<T> definition = this.definitions.getLast();
        if (definition.version() != version) {
            for (final DataDefinition<T> oldDefinition : this.definitions) {
                if (oldDefinition.version() != version) {
                    continue;
                }
                definition = oldDefinition;
                break;
            }
        }
        if (definition.version() != version) {
            throw new IllegalStateException("No data definition for version " + version + " for " + this.getClass().getName());
        }
        return definition.read(reader);
    }

    private void checkDefinitions() {
        if (this.definitions.isEmpty()) {
            throw new IllegalStateException("No data definition for " + this.getClass().getName());
        }
    }
}
