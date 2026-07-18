package net.desolatesky.data.reader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public final class InputStreamReader implements DataReader {

    private final ByteArrayInputStream stream;

    public InputStreamReader(ByteArrayInputStream stream) {
        this.stream = stream;
    }

    @Override
    public byte[] readByteArray() throws IOException {
        final byte[] lengthBytes = this.stream.readNBytes(Integer.BYTES);
        final ByteBuffer buffer = createBuffer(lengthBytes.length);
        buffer.put(lengthBytes);
        buffer.position(0);
        final int length = buffer.getInt();
        return this.stream.readNBytes(length);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return this.readByte() != 0;
    }

    @Override
    public byte readByte() throws IOException {
        return this.stream.readNBytes(1)[0];
    }

    @Override
    public UUID readUUID() throws IOException {
        final long leastSig = this.readLong();
        final long mostSig = this.readLong();
        return new UUID(mostSig, leastSig);
    }

    @Override
    public short readShort() throws IOException {
        final byte[] bytes = this.stream.readNBytes(Short.BYTES);
        final ByteBuffer buffer = createBuffer(bytes.length);
        buffer.put(bytes);
        buffer.position(0);
        return buffer.getShort();
    }

    @Override
    public int readInt() throws IOException {
        final byte[] bytes = this.stream.readNBytes(Integer.BYTES);
        final ByteBuffer buffer = createBuffer(bytes.length);
        buffer.put(bytes);
        buffer.position(0);
        return buffer.getInt();
    }

    @Override
    public long readLong() throws IOException {
        final byte[] bytes = this.stream.readNBytes(Long.BYTES);
        final ByteBuffer buffer = createBuffer(bytes.length);
        buffer.put(bytes);
        buffer.position(0);
        return buffer.getLong();
    }

    @Override
    public float readFloat() throws IOException {
        final byte[] bytes = this.stream.readNBytes(Float.BYTES);
        final ByteBuffer buffer = createBuffer(bytes.length);
        buffer.put(bytes);
        buffer.position(0);
        return buffer.getFloat();
    }

    @Override
    public double readDouble() throws IOException {
        final byte[] bytes = this.stream.readNBytes(Double.BYTES);
        final ByteBuffer buffer = createBuffer(bytes.length);
        buffer.put(bytes);
        buffer.position(0);
        return buffer.getDouble();
    }

    @Override
    public String readString() throws IOException {
        return new String(this.readByteArray(), StandardCharsets.UTF_32);
    }

    private static ByteBuffer createBuffer(int size) {
        final ByteBuffer buffer = ByteBuffer.allocate(size);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return buffer;
    }
}
