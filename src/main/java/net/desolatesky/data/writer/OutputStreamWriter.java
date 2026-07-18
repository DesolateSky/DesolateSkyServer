package net.desolatesky.data.writer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public final class OutputStreamWriter implements DataWriter {

    private final ByteArrayOutputStream stream;

    public OutputStreamWriter(ByteArrayOutputStream stream) {
        this.stream = stream;
    }

    @Override
    public void write(byte[] b) throws IOException {
        final ByteBuffer buffer = createBuffer(Integer.BYTES);
        buffer.putInt(b.length);
        buffer.position(0);
        this.stream.write(buffer.array());
        this.stream.write(b);
    }

    @Override
    public void write(boolean b) throws IOException {
        this.stream.write(b ? (byte) 1 : (byte) 0);
    }

    @Override
    public void write(byte b) throws IOException {
        this.stream.write(b);
    }

    @Override
    public void write(UUID uuid) throws IOException {
        this.write(uuid.getLeastSignificantBits());
        this.write(uuid.getMostSignificantBits());
    }

    @Override
    public void write(short s) throws IOException {
        final ByteBuffer buffer = createBuffer(Short.BYTES);
        buffer.putShort(s);
        this.stream.write(buffer.array());
    }

    @Override
    public void write(int i) throws IOException {
        final ByteBuffer buffer = createBuffer(Integer.BYTES);
        buffer.putInt(i);
        this.stream.write(buffer.array());
    }

    @Override
    public void write(long l) throws IOException {
        final ByteBuffer buffer = createBuffer(Long.BYTES);
        buffer.putLong(l);
        this.stream.write(buffer.array());
    }

    @Override
    public void write(float f) throws IOException {
        final ByteBuffer buffer = createBuffer(Float.BYTES);
        buffer.putFloat(f);
        this.stream.write(buffer.array());
    }

    @Override
    public void write(double d) throws IOException {
        final ByteBuffer buffer = createBuffer(Double.BYTES);
        buffer.putDouble(d);
        this.stream.write(buffer.array());
    }

    @Override
    public void write(String string) throws IOException {
        this.write(string.getBytes(StandardCharsets.UTF_32));
    }

    private static ByteBuffer createBuffer(int size) {
        final ByteBuffer buffer = ByteBuffer.allocate(size);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return buffer;
    }
}
