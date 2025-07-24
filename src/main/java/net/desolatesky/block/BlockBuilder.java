package net.desolatesky.block;

import net.desolatesky.block.property.type.BlockProperty;
import net.desolatesky.item.DSItem;
import net.kyori.adventure.key.Key;
import net.minestom.server.instance.block.Block;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.Nullable;

public class BlockBuilder {

    private Block current;

    public static BlockBuilder from(DSBlock dsBlock) {
        return new BlockBuilder(dsBlock.create());
    }

    public static BlockBuilder from(Block block) {
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

    public BlockBuilder breakTime(int millis) {
        this.current = this.current.withTag(BlockTags.BREAK_TIME, millis);
        return this;
    }

    public BlockBuilder blockItem(DSItem item) {
        this.current = this.current.withTag(BlockTags.BLOCK_ITEM, item.key());
        return this;
    }

    public BlockBuilder blockItem(Key key) {
        this.current = this.current.withTag(BlockTags.BLOCK_ITEM, key);
        return this;

    }

    public BlockBuilder blockItem() {
        Key blockId = this.current.getTag(BlockTags.ID);
        if (blockId == null) {
            blockId = this.current.key();
        }
        this.current = this.current.withTag(BlockTags.BLOCK_ITEM, blockId);
        return this;
    }

    public Block build() {
        return this.current;
    }

}
