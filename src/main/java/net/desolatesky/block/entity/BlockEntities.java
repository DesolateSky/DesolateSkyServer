package net.desolatesky.block.entity;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.BlockKeys;
import net.desolatesky.block.DSBlocks;
import net.desolatesky.block.entity.custom.ComposterBlockEntity;
import net.desolatesky.block.entity.custom.DebrisCatcherBlockEntity;
import net.desolatesky.block.entity.custom.SifterBlockEntity;
import net.desolatesky.block.entity.custom.crop.CropBlockEntity;
import net.kyori.adventure.key.Key;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.instance.block.BlockManager;
import org.jetbrains.annotations.Nullable;

public final class BlockEntities {

    public static BlockEntities create(BlockManager blockManager) {
        return new BlockEntities(blockManager);
    }

    private final BlockManager blockManager;

    private BlockEntities(BlockManager blockManager) {
        this.blockManager = blockManager;
    }

    public void register(BlockManager blockManager, DesolateSkyServer server) {
        blockManager.registerHandler(BlockKeys.DEBRIS_CATCHER, () -> new DebrisCatcherBlockEntity(server));
        blockManager.registerHandler(BlockKeys.SIFTER, () -> new SifterBlockEntity(server));
        blockManager.registerHandler(BlockKeys.COMPOSTER, () -> new ComposterBlockEntity(server));
        blockManager.registerHandler(BlockKeys.WHEAT, () -> new CropBlockEntity<>(BlockKeys.WHEAT, server));
    }

    public @Nullable BlockEntity<?> getBlockEntity(Key key) {
        final BlockHandler handler = this.blockManager.getHandler(key.asString());
        if (!(handler instanceof BlockEntity<?> blockEntity)) {
            return null;
        }
        return blockEntity;
    }

}
