package com.fisherl.desolatesky.data.reader;

import java.nio.ByteBuffer;

public interface DataReader {

    static DataReader newByteReader(ByteBuffer buffer) {
        return new ByteBufferReader(buffer);
    }

    byte[] readByteArray();

    byte readByte();

    short readShort();

    int readInt();

    long readLong();

    float readFloat();

    double readDouble();

    String readString();
}
