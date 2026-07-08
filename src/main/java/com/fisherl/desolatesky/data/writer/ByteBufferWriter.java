package com.fisherl.desolatesky.data.writer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class ByteBufferWriter implements DataWriter {

    private final ByteBuffer buffer;

    public ByteBufferWriter(ByteBuffer buffer) {
        this.buffer = buffer;
        this.buffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    @Override
    public void write(byte[] b) {
        this.buffer.putInt(b.length);
        this.buffer.put(b);
    }

    @Override
    public void write(byte b) {
        this.buffer.put(b);
    }

    @Override
    public void write(short s) {
        this.buffer.putShort(s);
    }

    @Override
    public void write(int i) {
        this.buffer.putInt(i);
    }

    @Override
    public void write(long l) {
        this.buffer.putLong(l);
    }

    @Override
    public void write(float f) {
        this.buffer.putFloat(f);
    }

    @Override
    public void write(double d) {
        this.buffer.putDouble(d);
    }

    @Override
    public void write(String string) {
        this.buffer.putInt(string.length());
        this.buffer.put(string.getBytes());
    }
}
