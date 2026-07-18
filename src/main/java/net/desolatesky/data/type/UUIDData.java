package net.desolatesky.data.type;

import net.desolatesky.data.reader.DataReader;
import net.desolatesky.data.writer.DataWriter;

import java.io.IOException;
import java.util.UUID;

public final class UUIDData implements Data<UUID> {

    UUIDData() {
    }

    @Override
    public void write(DataWriter writer, UUID value) throws IOException {
        writer.write(value);
    }

    @Override
    public UUID read(DataReader reader) throws IOException {
        return reader.readUUID();
    }
}
