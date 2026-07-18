package net.desolatesky.data.type;

import net.desolatesky.data.reader.DataReader;
import net.desolatesky.data.writer.DataWriter;

import java.io.IOException;

public final class ByteData implements Data<Byte> {

    ByteData() {}

    @Override
    public void write(DataWriter writer, Byte value) throws IOException {
        writer.write(value);
    }

    @Override
    public Byte read(DataReader reader) throws IOException {
        return reader.readByte();
    }
}
