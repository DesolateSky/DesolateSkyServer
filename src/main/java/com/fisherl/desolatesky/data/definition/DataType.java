package com.fisherl.desolatesky.data.definition;

import com.fisherl.desolatesky.data.reader.DataReader;
import com.fisherl.desolatesky.data.writer.DataWriter;

public interface DataType<T> {

    void write(DataWriter writer, T value);

    T read(DataReader reader);

    DataType<byte[]> BYTE_ARRAY = new DataType<>() {
        @Override
        public void write(DataWriter writer, byte[] value) {
            writer.write(value);
        }

        @Override
        public byte[] read(DataReader reader) {
            return reader.readByteArray();
        }
    };

    DataType<Byte> BYTE = new DataType<>() {
        @Override
        public void write(DataWriter writer, Byte value) {
            writer.write(value);
        }

        @Override
        public Byte read(DataReader reader) {
            return reader.readByte();
        }
    };

    DataType<Short> SHORT = new DataType<>() {
        @Override
        public void write(DataWriter writer, Short value) {
            writer.write(value);
        }

        @Override
        public Short read(DataReader reader) {
            return reader.readShort();
        }
    };

    DataType<Integer> INT = new DataType<>() {
        @Override
        public void write(DataWriter writer, Integer value) {
            writer.write(value);
        }

        @Override
        public Integer read(DataReader reader) {
            return reader.readInt();
        }
    };

    DataType<Long> LONG = new DataType<>() {
        @Override
        public void write(DataWriter writer, Long value) {
            writer.write(value);
        }

        @Override
        public Long read(DataReader reader) {
            return reader.readLong();
        }
    };

    DataType<Float> FLOAT = new DataType<>() {
        @Override
        public void write(DataWriter writer, Float value) {
            writer.write(value);
        }

        @Override
        public Float read(DataReader reader) {
            return reader.readFloat();
        }
    };

    DataType<Double> DOUBLE = new DataType<>() {
        @Override
        public void write(DataWriter writer, Double value) {
            writer.write(value);
        }

        @Override
        public Double read(DataReader reader) {
            return reader.readDouble();
        }
    };

    DataType<String> STRING = new DataType<String>() {
        @Override
        public void write(DataWriter writer, String value) {
            writer.write(value);
        }

        @Override
        public String read(DataReader reader) {
            return reader.readString();
        }
    };
}
