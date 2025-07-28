package net.desolatesky.block;

import com.google.common.base.Preconditions;
import net.desolatesky.block.handler.BlockHandlers;
import net.desolatesky.block.handler.DSBlockHandler;
import net.desolatesky.block.loot.BlockLootRegistry;
import net.kyori.adventure.key.Key;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.Nullable;
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
        return new DSBlockRegistry(blocksByKey, blockHandlers, blocks, blockLootRegistry);
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
        Preconditions.checkArgument(this.blockHandlers.getHandlerForBlock(block) != null, "Block handler for block %s is not registered", block.key());
        this.blocksByKey.put(block.key(), block);
    }

    public @UnknownNullability Block create(Key key) {
        final DSBlock block = this.blocksByKey.get(key);
        if (block == null) {
            return Block.fromKey(key);
        }
        return block.create(this.blockHandlers);
    }

    public Block create(Block block) {
        final DSBlock dsBlock = this.blocksByKey.get(block.key());
        if (dsBlock == null) {
            return block;
        }
        return dsBlock.create(this.blockHandlers);
    }

    public @Nullable DSBlock getBlock(Key key) {
        return this.blocksByKey.get(key);
    }

    public @UnmodifiableView Collection<Key> getKeys() {
        return this.blocksByKey.keySet();
    }

    public BlockLootRegistry blockLootRegistry() {
        return this.blockLootRegistry;
    }

}
