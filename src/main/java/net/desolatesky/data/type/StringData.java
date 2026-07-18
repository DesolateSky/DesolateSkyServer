package net.desolatesky.data.type;

import net.desolatesky.data.reader.DataReader;
import net.desolatesky.data.writer.DataWriter;

import java.io.IOException;

public final class StringData implements Data<String> {

    StringData() {
    }

    @Override
    public void write(DataWriter writer, String value) throws IOException {
        writer.write(value);
    }

    @Override
    public String read(DataReader reader) throws IOException{
        return reader.readString();
    }
}
