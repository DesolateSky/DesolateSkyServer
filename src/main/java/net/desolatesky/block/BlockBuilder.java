package net.desolatesky.block;

import net.desolatesky.block.property.type.BlockProperty;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.Nullable;

public class BlockBuilder {

    private Block current;

    private BlockBuilder(Block block) {
        this.current = block;
    }

    public static BlockBuilder of(DSBlocks blocks, Block block) {
        return new BlockBuilder(blocks.get(block));
    }

    public static BlockBuilder of(DSBlocks blocks, Block block, BlockHandler blockHandler) {
        return new BlockBuilder(blocks.get(block, blockHandler));
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
