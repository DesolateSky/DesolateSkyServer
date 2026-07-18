package net.desolatesky.data.type;

import net.desolatesky.data.reader.DataReader;
import net.desolatesky.data.writer.DataWriter;

import java.io.IOException;

public final class FloatData implements Data<Float> {

    FloatData() {}

    @Override
    public void write(DataWriter writer, Float value) throws IOException {
        writer.write(value);
    }

    @Override
    public Float read(DataReader reader) throws IOException {
        return reader.readFloat();
    }
}
