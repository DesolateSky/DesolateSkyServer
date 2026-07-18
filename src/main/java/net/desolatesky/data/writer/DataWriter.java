package net.desolatesky.data.writer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

public interface DataWriter {

    static DataWriter newByteWriter(ByteArrayOutputStream stream) {
        return new OutputStreamWriter(stream);
    }

    void write(byte[] b) throws IOException;

    void write(boolean b) throws IOException;

    void write(byte b) throws IOException;

    void write(UUID uuid) throws IOException;

    void write(short s) throws IOException;

    void write(int i) throws IOException;

    void write(long l) throws IOException;

    void write(float f) throws IOException;

    void write(double d) throws IOException;

    void write(String string) throws IOException;

}
