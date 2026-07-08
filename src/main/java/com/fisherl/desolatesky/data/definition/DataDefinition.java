package com.fisherl.desolatesky.data.definition;

import com.fisherl.desolatesky.data.reader.DataReader;
import com.fisherl.desolatesky.data.writer.DataWriter;

public abstract class DataDefinition<T> {

    private final int version;

    public DataDefinition(int version) {
        this.version = version;
    }

    public abstract void write(DataWriter reader, T value);

    public abstract T read(DataReader reader);

    public int version() {
        return this.version;
    }

}
