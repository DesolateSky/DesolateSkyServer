package net.desolatesky.data.type;

import net.desolatesky.data.reader.DataReader;
import net.desolatesky.data.writer.DataWriter;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.TagStringIO;
import net.kyori.adventure.text.Component;
import net.minestom.server.adventure.serializer.nbt.NbtComponentSerializer;

import java.io.IOException;

public final class ComponentData implements Data<Component> {

    ComponentData() {
    }

    @Override
    public void write(DataWriter writer, Component value) throws IOException {
        final BinaryTag tag = NbtComponentSerializer.nbt().serialize(value);
        final String string = TagStringIO.tagStringIO().asString(tag);
        Data.STRING.write(writer, string);
    }

    @Override
    public Component read(DataReader reader) throws IOException {
        final String string = reader.readString();
        final BinaryTag tag = TagStringIO.tagStringIO().asTag(string);
        return NbtComponentSerializer.nbt().deserialize(tag);
    }
}
