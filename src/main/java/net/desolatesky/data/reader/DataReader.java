package net.desolatesky.data.reader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

public interface DataReader {

    static DataReader newByteReader(ByteArrayInputStream stream) {
        return new InputStreamReader(stream);
    }

    byte[] readByteArray() throws IOException;

    boolean readBoolean() throws IOException;

    byte readByte() throws IOException;

    UUID readUUID() throws IOException;

    short readShort() throws IOException;

    int readInt() throws IOException;

    long readLong() throws IOException;

    float readFloat() throws IOException;

    double readDouble() throws IOException;

    String readString() throws IOException;

}
