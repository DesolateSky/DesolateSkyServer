package net.desolatesky.tag;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.tag.Tag;

public final class TagBuilder {

    private CompoundBinaryTag.Builder tag;

    public static TagBuilder create(CompoundBinaryTag tag) {
        return new TagBuilder(CompoundBinaryTag.builder().put(tag));
    }

    public static TagBuilder create(CompoundBinaryTag.Builder tag) {
        return new TagBuilder(tag);
    }

    public static TagBuilder create() {
        return new TagBuilder(CompoundBinaryTag.builder());
    }

    private TagBuilder(CompoundBinaryTag.Builder tag) {
        this.tag = tag;
    }

    public <T> TagBuilder with(Tag<T> tag, T value) {
        tag.write(this.tag, value);
        return this;
    }

    public CompoundBinaryTag build() {
        return this.tag.build();
    }

}
