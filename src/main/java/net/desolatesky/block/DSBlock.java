package net.desolatesky.block;

import net.desolatesky.block.entity.BlockEntities;
import net.desolatesky.block.entity.BlockEntity;
import net.desolatesky.block.handler.DSBlockHandler;
import net.desolatesky.block.settings.BlockSettings;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

public final class DSBlock implements Keyed {

    private final Key key;
    private final Block block;
    private final DSBlockHandler blockHandler;

    public static DSBlock create(Block block, DSBlockHandler blockHandler) {
        return create(block.key(), block, blockHandler);
    }

    public static DSBlock create(Key key, Block block, DSBlockHandler blockHandler) {
        if (!key.equals(block.key())) {
            block = block.withTag(BlockTags.ID, key);
        }
        return new DSBlock(key, block, blockHandler);
    }

    private DSBlock(Key key, Block block, DSBlockHandler blockHandler) {
        this.key = key;
        this.block = block;
        this.blockHandler = blockHandler;
    }

    public Block create(BlockEntities blockEntities) {
        final BlockEntity<?> handler = blockEntities.getBlockEntity(this.key);
        if (handler == null) {
            return this.block;
        }
        return this.block.withHandler(handler);
    }

    public BlockBuilder createBuilder(BlockEntities blockEntities) {
        final Block block = this.create(blockEntities);
        return BlockBuilder.blockBuilder(block);
    }

    public @NotNull BlockSettings settings() {
        return this.handler().settings();
    }

    public DSBlockHandler handler() {
        return this.blockHandler;
    }

    public boolean is(Block block) {
        return this.key.equals(getIdFor(block));
    }

    @Override
    public @NotNull Key key() {
        return this.key;
    }

    public static Key getIdFor(Block block) {
        final Key id = block.getTag(BlockTags.ID);
        if (id != null) {
            return id;
        }
        return block.key();
    }

}
