package net.desolatesky.block;

import net.desolatesky.block.handler.BlockHandlerSupplier;
import net.desolatesky.block.handler.DSBlockHandler;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public final class DSBlock implements Keyed {

    private final Key key;
    private final Supplier<Block> blockSupplier;

    public static DSBlock create(Block block) {
        return create(block.key(), block, null);
    }

    public static DSBlock create(Block block, @Nullable BlockHandlerSupplier<? extends DSBlockHandler> blockHandlerSupplier) {
        return create(block.key(), block, blockHandlerSupplier);
    }

    public static DSBlock create(Key key, Block block, @Nullable BlockHandlerSupplier<? extends DSBlockHandler> blockHandlerSupplier) {
        if (!key.equals(block.key())) {
            block = block.withTag(BlockTags.ID, key);
        }
        if (blockHandlerSupplier == null) {
            final Block result = block;
            return new DSBlock(key, () -> result);
        }
        final DSBlockHandler handler = blockHandlerSupplier.get();
        if (handler.stateless()) {
            final Block blockResult = block.withHandler(handler);
            return new DSBlock(key, () -> blockResult);
        }
        final Block result = block;
        return new DSBlock(key, () -> result.withHandler(blockHandlerSupplier.get()));
    }

    public DSBlock(Key key, Supplier<Block> blockSupplier) {
        this.key = key;
        this.blockSupplier = blockSupplier;
    }

    public Block create() {
        return this.blockSupplier.get();
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
