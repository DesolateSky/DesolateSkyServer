package net.desolatesky.block;

import net.desolatesky.block.handler.BlockHandlers;
import net.desolatesky.block.handler.DSBlockHandler;
import net.desolatesky.block.loot.BlockLootRegistry;
import net.kyori.adventure.key.Key;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.UnknownNullability;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Map;

public final class DSBlockRegistry {

    private final Map<Key, DSBlock> blocksByKey;
    private final BlockHandlers blockHandlers;
    private final DSBlocks blocks;
    private final BlockLootRegistry blockLootRegistry;

    public static DSBlockRegistry create(Map<Key, DSBlock> blocksByKey, BlockHandlers blockHandlers, DSBlocks blocks, BlockLootRegistry blockLootRegistry) {
        final DSBlockRegistry registry = new DSBlockRegistry(blocksByKey, blockHandlers, blocks, blockLootRegistry);
        blocks.register(registry);
        return registry;
    }

    private DSBlockRegistry(Map<Key, DSBlock> blocksByKey, BlockHandlers blockHandlers, DSBlocks blocks, BlockLootRegistry blockLootRegistry) {
        this.blocksByKey = blocksByKey;
        this.blockHandlers = blockHandlers;
        this.blocks = blocks;
        this.blockLootRegistry = blockLootRegistry;
    }

    public  BlockHandlers blockHandlers() {
        return this.blockHandlers;
    }

    public DSBlocks blocks() {
        return this.blocks;
    }

    public void register(DSBlock block) {
        this.blocksByKey.put(block.key(), block);
    }

    public @UnknownNullability Block create(Key key) {
        final DSBlock block = this.blocksByKey.get(key);
        if (block == null) {
            return Block.fromKey(key);
        }
        return block.create();
    }

    public Block create(Block block) {
        final DSBlock dsBlock = this.blocksByKey.get(block.key());
        if (dsBlock == null) {
            return block;
        }
        return dsBlock.create();
    }

    public Block create(Block block, DSBlockHandler handler) {
        final DSBlock dsBlock = this.blocksByKey.get(block.key());
        if (dsBlock == null) {
            return block.withHandler(handler);
        }
        return dsBlock.create().withHandler(handler);
    }

    public @UnmodifiableView Collection<Key> getKeys() {
        return this.blocksByKey.keySet();
    }

    public BlockLootRegistry blockLootRegistry() {
        return this.blockLootRegistry;
    }

}
