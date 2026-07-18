package net.desolatesky.data.type;

import net.desolatesky.data.reader.DataReader;
import net.desolatesky.data.writer.DataWriter;

import java.io.IOException;

public final class ShortData implements Data<Short> {

    ShortData() {}

    @Override
    public void write(DataWriter writer, Short value) throws IOException {
        writer.write(value);
    }

    @Override
    public Short read(DataReader reader) throws IOException {
        return reader.readShort();
    }
}
