package net.desolatesky.data.definition;

import net.desolatesky.data.reader.DataReader;
import net.desolatesky.data.writer.DataWriter;

import java.io.IOException;

public abstract class DataDefinition<T> {

    private final int version;

    public DataDefinition(int version) {
        this.version = version;
    }

    public abstract void write(DataWriter writer, T value) throws IOException;

    public abstract T read(DataReader reader) throws IOException;

    public int version() {
        return this.version;
    }

}
