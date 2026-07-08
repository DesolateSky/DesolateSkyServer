package com.fisherl.desolatesky.data.reader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class ByteBufferReader implements DataReader {

    private final ByteBuffer buffer;

    public ByteBufferReader(ByteBuffer buffer) {
        this.buffer = buffer;
        this.buffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    @Override
    public byte[] readByteArray() {
        final int length = this.buffer.getInt();
        final byte[] output = new byte[length];
        this.buffer.get(output);
        return output;
    }

    @Override
    public byte readByte() {
        return this.buffer.get();
    }

    @Override
    public short readShort() {
        return this.buffer.getShort();
    }

    @Override
    public int readInt() {
        return this.buffer.getInt();
    }

    @Override
    public long readLong() {
        return this.buffer.getLong();
    }

    @Override
    public float readFloat() {
        return this.buffer.getFloat();
    }

    @Override
    public double readDouble() {
        return this.buffer.getDouble();
    }

    @Override
    public String readString() {
        final int length = this.buffer.getInt();
        final byte[] result = new byte[length];
        this.buffer.get(result);
        return new String(result);
    }
}
