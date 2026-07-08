package com.fisherl.desolatesky.data.writer;

import java.nio.ByteBuffer;

public interface DataWriter {

    static DataWriter newByteWriter(ByteBuffer buffer) {
        return new ByteBufferWriter(buffer);
    }

    void write(byte[] b);

    void write(byte b);

    void write(short s);

    void write(int i);

    void write(long l);

    void write(float f);

    void write(double d);

    void write(String string);
}
