package net.desolatesky.block.handler.entity;

import net.desolatesky.block.entity.BlockEntity;
import net.desolatesky.block.handler.BlockHandlerResult;
import net.desolatesky.block.handler.DSBlockHandler;
import net.desolatesky.block.settings.BlockSettings;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.player.DSPlayer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.BlockHandler;

public abstract class BlockEntityHandler<T extends BlockEntity<T>> extends DSBlockHandler {

    private final Class<T> entityClass;

    public BlockEntityHandler(BlockSettings blockSettings, Class<T> entityClass) {
        super(blockSettings);
        this.entityClass = entityClass;
    }

    @Override
    public final BlockHandlerResult.Place onPlace(DSInstance instance, Block block, Point blockPosition) {
        final BlockHandler blockHandler = block.handler();
        if (!this.entityClass.isInstance(blockHandler)) {
            return BlockHandlerResult.passthroughPlace(block);
        }
        return this.onPlace(instance, block, blockPosition, this.entityClass.cast(blockHandler));
    }

    /**
     * Override this method to handle the placement of the block entity.
     */
    public BlockHandlerResult.Place onPlace(DSInstance instance, Block block, Point blockPosition, T entity) {
        return BlockHandlerResult.passthroughPlace(block);
    }

    @Override
    public final BlockHandlerResult.Place onPlayerPlace(DSPlayer player, DSInstance instance, Block block, Point blockPosition, PlayerHand hand, BlockFace face, Point cursorPosition) {
        final BlockHandler blockHandler = block.handler();
        if (!this.entityClass.isInstance(blockHandler)) {
            return BlockHandlerResult.passthroughPlace(block);
        }
        return this.onPlayerPlace(player, instance, block, blockPosition, hand, face, cursorPosition, this.entityClass.cast(blockHandler));
    }

    /**
     * Override this method to handle the placement of the block entity by a player.
     */
    public BlockHandlerResult.Place onPlayerPlace(DSPlayer player, DSInstance instance, Block block, Point blockPosition, PlayerHand hand, BlockFace face, Point cursorPosition, T entity) {
        return BlockHandlerResult.passthroughPlace(block);
    }

    @Override
    public final BlockHandlerResult onDestroy(DSInstance instance, Block block, Point blockPosition) {
        final BlockHandler blockHandler = block.handler();
        if (!this.entityClass.isInstance(blockHandler)) {
            return BlockHandlerResult.PASS_THROUGH;
        }
        return this.onDestroy(instance, block, blockPosition, this.entityClass.cast(blockHandler));
    }

    /**
     * Override this method to handle the destruction of the block entity.
     */
    public BlockHandlerResult onDestroy(DSInstance instance, Block block, Point blockPosition, T entity) {
        return BlockHandlerResult.PASS_THROUGH;
    }

    @Override
    public final BlockHandlerResult onPlayerDestroy(DSPlayer player, DSInstance instance, Block block, Point blockPosition) {
        final BlockHandler blockHandler = block.handler();
        if (!this.entityClass.isInstance(blockHandler)) {
            return BlockHandlerResult.PASS_THROUGH;
        }
        return this.onPlayerDestroy(player, instance, block, blockPosition, this.entityClass.cast(blockHandler));
    }

    /**
     * Override this method to handle the destruction of the block entity by a player.
     */
    public BlockHandlerResult onPlayerDestroy(DSPlayer player, DSInstance instance, Block block, Point blockPosition, T entity) {
        return BlockHandlerResult.PASS_THROUGH;
    }

    @Override
    public final BlockHandlerResult onPlayerInteract(Player player, DSInstance instance, Block block, Point blockPosition, PlayerHand hand, BlockFace face, Point cursorPosition) {
        final BlockHandler blockHandler = block.handler();
        if (!this.entityClass.isInstance(blockHandler)) {
            return BlockHandlerResult.PASS_THROUGH;
        }
        return this.onPlayerInteract((DSPlayer) player, instance, block, blockPosition, hand, face, cursorPosition, this.entityClass.cast(blockHandler));
    }

    /**
     * Override this method to handle player interactions with the block entity.
     */
    public BlockHandlerResult onPlayerInteract(DSPlayer player, DSInstance instance, Block block, Point blockPosition, PlayerHand hand, BlockFace face, Point cursorPosition, T entity) {
        return BlockHandlerResult.PASS_THROUGH;
    }

    @Override
    public final BlockHandlerResult onBlockEntityInteract(BlockEntity<?> actor, DSInstance instance, Block block, Point blockPosition, BlockFace face) {
        if (!this.entityClass.isInstance(actor)) {
            return BlockHandlerResult.PASS_THROUGH;
        }
        return this.onBlockEntityInteract(this.entityClass.cast(actor), instance, block, blockPosition, face, this.entityClass.cast(block.handler()));
    }

    /**
     * Override this method to handle interactions with the block entity by another block entity.
     */
    public BlockHandlerResult onBlockEntityInteract(BlockEntity<?> actor, DSInstance instance, Block block, Point blockPosition, BlockFace face, T entity) {
        return BlockHandlerResult.PASS_THROUGH;
    }

    @Override
    public final void onTick(DSInstance instance, Block block, Point blockPosition) {
        final BlockHandler blockHandler = block.handler();
        if (!this.entityClass.isInstance(blockHandler)) {
            return;
        }
        this.onTick(instance, block, blockPosition, this.entityClass.cast(blockHandler));
    }

    /**
     * Override this method to handle the tick event for the block entity.
     */
    public void onTick(DSInstance instance, Block block, Point blockPosition, T entity) {

    }

    @Override
    public final void onRandomTick(DSInstance instance, Block block, Point blockPosition) {
        final BlockHandler blockHandler = block.handler();
        if (!this.entityClass.isInstance(blockHandler)) {
            return;
        }
        this.onRandomTick(instance, block, blockPosition, this.entityClass.cast(blockHandler));
    }

    /**
     * Override this method to handle the random tick event for the block entity.
     */
    public void onRandomTick(DSInstance instance, Block block, Point blockPosition, T entity) {

    }

    public Class<T> entityClass() {
        return this.entityClass;
    }

}
