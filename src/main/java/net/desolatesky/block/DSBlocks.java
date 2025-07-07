package net.desolatesky.block;

import net.desolatesky.registry.DSRegistry;
import net.kyori.adventure.key.Key;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;

import java.util.HashMap;
import java.util.Map;

public final class DSBlocks extends DSRegistry<Block> {

    private static final DSBlocks INSTANCE = new DSBlocks();

    public static DSBlocks get() {
        return INSTANCE;
    }

    private DSBlocks() {
        super(BlockTags.ID);
    }

    @Override
    protected <T> Block withTag(Block block, Tag<T> tag, T value) {
        return block.withTag(tag, value);
    }

    @Override
    protected Block getDefault(Key key) {
        return Block.fromKey(key);
    }

    @Override
    protected <T> T getTag(Block block, Tag<T> tag) {
        return block.getTag(tag);
    }

    public Block get(Block block, BlockHandler blockHandler) {
        final Key key = this.getId(block);
        return this.elements.computeIfAbsent(key, k -> block.withHandler(blockHandler));
    }

    @Override
    protected Key getId(Block block) {
        final Key key = this.getTag(block, this.idTag);
        if (key == null) {
            return block.key();
        }
        return key;
    }

}
