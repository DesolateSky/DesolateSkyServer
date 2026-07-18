package net.desolatesky.data.type;

import net.desolatesky.data.reader.DataReader;
import net.desolatesky.data.writer.DataWriter;

import java.io.IOException;

public final class IntegerData implements Data<Integer> {

    IntegerData() {}

    @Override
    public void write(DataWriter writer, Integer value) throws IOException {
        writer.write(value);
    }

    @Override
    public Integer read(DataReader reader) throws IOException {
        return reader.readInt();
    }
}
