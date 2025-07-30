package net.desolatesky.block;

import net.desolatesky.block.property.type.BlockProperty;
import net.minestom.server.instance.block.Block;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.Nullable;

public class BlockBuilder {

    private Block current;

    public static BlockBuilder blockBuilder(Block block) {
        return new BlockBuilder(block);
    }

    private BlockBuilder(Block block) {
        this.current = block;
    }

    public <T> BlockBuilder property(BlockProperty<T> property, T value) {
        this.current = property.set(this.current, value);
        return this;
    }

    public <T> BlockBuilder tag(Tag<T> tag, @Nullable T value) {
        this.current = this.current.withTag(tag, value);
        return this;
    }

    public Block build() {
        return this.current;
    }

}
