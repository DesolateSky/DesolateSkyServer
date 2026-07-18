package net.desolatesky.data.type;

import net.desolatesky.data.reader.DataReader;
import net.desolatesky.data.writer.DataWriter;

import java.io.IOException;

public final class BooleanData implements Data<Boolean> {

    BooleanData() {
    }

    @Override
    public void write(DataWriter writer, Boolean value) throws IOException {
        writer.write(value);
    }

    @Override
    public Boolean read(DataReader reader) throws IOException {
        return reader.readBoolean();
    }
}
