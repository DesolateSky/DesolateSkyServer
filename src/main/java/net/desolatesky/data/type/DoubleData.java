package net.desolatesky.data.type;

import net.desolatesky.data.reader.DataReader;
import net.desolatesky.data.writer.DataWriter;

import java.io.IOException;

public final class DoubleData implements Data<Double> {

    DoubleData() {
    }

    @Override
    public void write(DataWriter writer, Double value) throws IOException {
        writer.write(value);
    }

    @Override
    public Double read(DataReader reader) throws IOException {
        return reader.readDouble();
    }
}
