package net.desolatesky.data.type;

import net.desolatesky.data.reader.DataReader;
import net.desolatesky.data.writer.DataWriter;

import java.io.IOException;

public final class EnumData<E extends Enum<E>> implements Data<E> {

    public static <E extends Enum<E>> EnumData<E> createEnumData(Class<E> enumClass) {
        return new EnumData<>(enumClass);
    }

    private final Class<E> enumClass;

    EnumData(Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public void write(DataWriter writer, E value) throws IOException {
        Data.STRING.write(writer, value.name());
    }

    @Override
    public E read(DataReader reader) throws IOException {
        final String name = reader.readString();
        return Enum.valueOf(this.enumClass, name);
    }
}
