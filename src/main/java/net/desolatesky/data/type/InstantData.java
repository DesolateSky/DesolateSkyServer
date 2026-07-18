package net.desolatesky.data.type;

import net.desolatesky.data.reader.DataReader;
import net.desolatesky.data.writer.DataWriter;

import java.io.IOException;
import java.time.Instant;

public final class InstantData implements Data<Instant> {

    InstantData() {}

    @Override
    public void write(DataWriter writer, Instant value) throws IOException {
        writer.write(value.toEpochMilli());
    }

    @Override
    public Instant read(DataReader reader) throws IOException {
        return Instant.ofEpochMilli(reader.readLong());
    }
}
