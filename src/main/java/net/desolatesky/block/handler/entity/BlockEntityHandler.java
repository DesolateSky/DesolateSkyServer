package net.desolatesky.block.handler.entity;

import net.desolatesky.block.handler.DSBlockHandler;
import net.desolatesky.block.entity.BlockEntity;
import net.desolatesky.block.handler.InteractionResult;
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
    public final void onPlace(DSInstance instance, Block block, Point blockPosition) {
        final BlockHandler blockHandler = block.handler();
        if (!this.entityClass.isInstance(blockHandler)) {
            return;
        }
        this.onPlace(instance, block, blockPosition, this.entityClass.cast(blockHandler));
    }

    /**
     * Override this method to handle the placement of the block entity.
     */
    public void onPlace(DSInstance instance, Block block, Point blockPosition, T entity) {
    }

    @Override
    public final void onPlayerPlace(DSPlayer player, DSInstance instance, Block block, Point blockPosition, PlayerHand hand, BlockFace face, Point cursorPosition) {
        final BlockHandler blockHandler = block.handler();
        if (!this.entityClass.isInstance(blockHandler)) {
            return;
        }
        this.onPlayerPlace(player, instance, block, blockPosition, hand, face, cursorPosition, this.entityClass.cast(blockHandler));
    }

    /**
     * Override this method to handle the placement of the block entity by a player.
     */
    public void onPlayerPlace(DSPlayer player, DSInstance instance, Block block, Point blockPosition, PlayerHand hand, BlockFace face, Point cursorPosition, T entity) {

    }

    @Override
    public final void onDestroy(DSInstance instance, Block block, Point blockPosition) {
        final BlockHandler blockHandler = block.handler();
        if (!this.entityClass.isInstance(blockHandler)) {
            return;
        }
        this.onDestroy(instance, block, blockPosition, this.entityClass.cast(blockHandler));
    }

    /**
     * Override this method to handle the destruction of the block entity.
     */
    public void onDestroy(DSInstance instance, Block block, Point blockPosition, T entity) {

    }

    @Override
    public final void onPlayerDestroy(DSPlayer player, DSInstance instance, Block block, Point blockPosition) {
        final BlockHandler blockHandler = block.handler();
        if (!this.entityClass.isInstance(blockHandler)) {
            return;
        }
        this.onPlayerDestroy(player, instance, block, blockPosition, this.entityClass.cast(blockHandler));
    }

    /**
     * Override this method to handle the destruction of the block entity by a player.
     */
    public void onPlayerDestroy(DSPlayer player, DSInstance instance, Block block, Point blockPosition, T entity) {

    }

    @Override
    public final InteractionResult onPlayerInteract(Player player, DSInstance instance, Block block, Point blockPosition, PlayerHand hand, BlockFace face, Point cursorPosition) {
        final BlockHandler blockHandler = block.handler();
        if (!this.entityClass.isInstance(blockHandler)) {
            return InteractionResult.PASSTHROUGH;
        }
        return this.onPlayerInteract((DSPlayer) player, instance, block, blockPosition, hand, face, cursorPosition, this.entityClass.cast(blockHandler));
    }

    /**
     * Override this method to handle player interactions with the block entity.
     */
    public InteractionResult onPlayerInteract(DSPlayer player, DSInstance instance, Block block, Point blockPosition, PlayerHand hand, BlockFace face, Point cursorPosition, T entity) {
        return InteractionResult.PASSTHROUGH;
    }

    @Override
    public final InteractionResult onBlockEntityInteract(BlockEntity<?> actor, DSInstance instance, Block block, Point blockPosition, BlockFace face) {
        if (!this.entityClass.isInstance(actor)) {
            return InteractionResult.PASSTHROUGH;
        }
        return this.onBlockEntityInteract(this.entityClass.cast(actor), instance, block, blockPosition, face, this.entityClass.cast(block.handler()));
    }

    /**
     * Override this method to handle interactions with the block entity by another block entity.
     */
    public InteractionResult onBlockEntityInteract(BlockEntity<?> actor, DSInstance instance, Block block, Point blockPosition, BlockFace face, T entity) {
        return InteractionResult.PASSTHROUGH;
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
