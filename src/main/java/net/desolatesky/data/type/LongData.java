package net.desolatesky.data.type;

import net.desolatesky.data.reader.DataReader;
import net.desolatesky.data.writer.DataWriter;

import java.io.IOException;

public final class LongData implements Data<Long> {

    LongData() {}

    @Override
    public void write(DataWriter writer, Long value) throws IOException {
        writer.write(value);
    }

    @Override
    public Long read(DataReader reader) throws IOException {
        return reader.readLong();
    }
}
