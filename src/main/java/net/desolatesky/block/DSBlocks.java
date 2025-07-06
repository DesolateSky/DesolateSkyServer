package net.desolatesky.block;

import net.kyori.adventure.key.Key;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;

import java.util.HashMap;
import java.util.Map;

public final class DSBlocks {

    private static final DSBlocks INSTANCE = new DSBlocks();

    public static DSBlocks get() {
        return INSTANCE;
    }

    private DSBlocks() {
    }

    private final Map<Key, Block> blocks = new HashMap<>();


    private Block register(Key id, Block block) {
        final Block actual = block.withTag(BlockTags.ID, id);
        this.blocks.put(id, actual);
        return actual;
    }

    private Block register(Block block) {
        return this.register(block.key(), block);
    }

    public Key getBlockId(Block block) {
        final Key id = block.getTag(BlockTags.ID);
        if (id == null) {
            return block.key();
        }
        return id;
    }

    public Block get(Key key) {
        return this.blocks.getOrDefault(key, Block.fromKey(key));
    }

    public Block get(Block block) {
        return this.get(this.getBlockId(block));
    }

    public Block get(Block block, BlockHandler blockHandler) {
        final Key key = this.getBlockId(block);
        return this.blocks.computeIfAbsent(key, k -> block.withHandler(blockHandler));
    }

}
