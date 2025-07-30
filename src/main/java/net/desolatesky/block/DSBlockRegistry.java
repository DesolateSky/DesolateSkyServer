package net.desolatesky.block;

import net.desolatesky.block.entity.BlockEntities;
import net.desolatesky.block.handler.DSBlockHandler;
import net.desolatesky.loot.table.LootTable;
import net.kyori.adventure.key.Key;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public final class DSBlockRegistry {

    private final Map<Key, DSBlock> blocksByKey;
    private final BlockEntities blockEntities;
    private final DSBlocks blocks;

    public static DSBlockRegistry create(Map<Key, DSBlock> blocksByKey, BlockEntities blockEntities, DSBlocks blocks) {
        return new DSBlockRegistry(blocksByKey, blockEntities, blocks);
    }

    private DSBlockRegistry(Map<Key, DSBlock> blocksByKey, BlockEntities blockEntities, DSBlocks blocks) {
        this.blocksByKey = blocksByKey;
        this.blockEntities = blockEntities;
        this.blocks = blocks;
    }

    public BlockEntities blockEntities() {
        return this.blockEntities;
    }

    public @Nullable DSBlockHandler getHandlerForBlock(Block block) {
        final DSBlock dsBlock = this.getBlock(block);
        if (dsBlock == null) {
            return null;
        }
        return dsBlock.handler();
    }

    public @Nullable LootTable getLootTableForBlock(Block block) {
        final DSBlock dsBlock = this.getBlock(block);
        if (dsBlock == null) {
            return null;
        }
        return dsBlock.settings().lootTable();
    }

    public LootTable getLootTableForBlock(Block block, LootTable defaultLootTable) {
        final DSBlock dsBlock = this.getBlock(block);
        if (dsBlock == null) {
            return defaultLootTable;
        }
        return dsBlock.settings().lootTable();
    }

    public DSBlocks blocks() {
        return this.blocks;
    }

    public void register(DSBlock block) {
        this.blocksByKey.put(block.key(), block);
    }

    public @UnknownNullability Block create(Key key) {
        final DSBlock block = this.getBlock(key);
        if (block == null) {
            return Block.fromKey(key);
        }
        return block.create(this.blockEntities);
    }

    public Block create(Block block) {
        final DSBlock dsBlock = this.blocksByKey.get(block.key());
        if (dsBlock == null) {
            return block;
        }
        return dsBlock.create(this.blockEntities);
    }

    public @Nullable DSBlock getBlock(Key key) {
        return this.blocksByKey.get(key);
    }

    public @Nullable DSBlock getBlock(Block block) {
        return this.blocksByKey.get(DSBlock.getIdFor(block));
    }

    public static Key getKeyFor(Block block) {
        return DSBlock.getIdFor(block);
    }

    public @UnmodifiableView Collection<Key> getKeys() {
        return this.blocksByKey.keySet();
    }

}
