package net.desolatesky.block;

import net.desolatesky.block.handler.BlockHandlers;
import net.desolatesky.block.handler.DSBlockHandler;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DSBlock implements Keyed {

    private final Key key;
    private final Block block;
    private @Nullable Block cachedBlock;

    public static DSBlock create(Block block) {
        return create(block.key(), block);
    }

    public static DSBlock create(Key key, Block block) {
        if (!key.equals(block.key())) {
            block = block.withTag(BlockTags.ID, key);
        }
        return new DSBlock(key, block);
    }

    private DSBlock(Key key, Block block) {
        this.key = key;
        this.block = block;
    }

    public Block create(BlockHandlers blockHandlers) {
        if (this.cachedBlock != null) {
            return this.cachedBlock;
        }
        final DSBlockHandler handler = blockHandlers.getHandlerForBlock(this);
        if (handler == null) {
            return this.block;
        }
        if (handler.stateless()) {
            this.cachedBlock = this.block.withHandler(handler);
            return this.cachedBlock;
        }
        return this.block.withHandler(handler);
    }

    public BlockBuilder createBuilder(BlockHandlers blockHandlers) {
        final Block block = this.create(blockHandlers);
        return BlockBuilder.from(block);
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
