package com.fisherl.desolatesky.data.definition;

import com.fisherl.desolatesky.data.reader.DataReader;
import com.fisherl.desolatesky.data.writer.DataWriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public final class DataTranslator<T> {

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

    public void write(DataWriter writer, T value) {
        if (this.definitions.isEmpty()) {
            throw new IllegalStateException("No data definition for " + this.getClass().getName());
        }
        final DataDefinition<T> definition = this.definitions.getLast();
        writer.write(definition.version());
        definition.write(writer, value);
    }

    public T read(DataReader reader) {
        if (this.definitions.isEmpty()) {
            throw new IllegalStateException("No data definition for " + this.getClass().getName());
        }
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
}
