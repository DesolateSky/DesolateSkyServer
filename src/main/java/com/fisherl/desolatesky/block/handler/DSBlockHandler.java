package com.fisherl.desolatesky.block.handler;

import com.fisherl.desolatesky.block.BlockFactory;
import com.fisherl.desolatesky.block.behavior.BlockBehavior;
import com.fisherl.desolatesky.util.BlockUtil;
import com.fisherl.desolatesky.world.DSWorld;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.Collection;

@NotNullByDefault
public final class DSBlockHandler implements BlockHandler {

    public static DSBlockHandler newTickingBlockHandler(Key key, BlockFactory blockFactory) {
        return new DSBlockHandler(key, blockFactory, true);
    }


    private final Key key;
    private final BlockFactory blockFactory;
    private final boolean ticks;

    public DSBlockHandler(Key key, BlockFactory blockFactory, boolean ticks) {
        this.key = key;
        this.blockFactory = blockFactory;
        this.ticks = ticks;
    }

    @Override
    public Key getKey() {
        return this.key;
    }

    @Override
    public void onPlace(Placement placement) {
        BlockHandler.super.onPlace(placement);
    }

    @Override
    public void onDestroy(Destroy destroy) {
        BlockHandler.super.onDestroy(destroy);
    }

    @Override
    public boolean onInteract(Interaction interaction) {
        return BlockHandler.super.onInteract(interaction);
    }

    @Override
    public void onTouch(Touch touch) {
        BlockHandler.super.onTouch(touch);
    }

    @Override
    public void tick(Tick tick) {
        final Block block =  tick.getBlock();
        final Point pos = tick.getBlockPosition();
        final Key blockId = BlockUtil.getBlockId(block);
        this.blockFactory.getBlockDefinition(blockId)
                .flatMap(definition -> definition.getBehavior(BlockBehavior.Type.TICK))
                .ifPresent(behavior -> {
                    if (!(tick.getInstance() instanceof final DSWorld world)) {
                        return;
                    }
                    behavior.onTick(world, pos, block, blockId);
                });
        BlockHandler.super.tick(tick);
    }

    @Override
    public boolean isTickable() {
        return this.ticks;
    }

    @Override
    public Collection<Tag<?>> getBlockEntityTags() {
        return BlockHandler.super.getBlockEntityTags();
    }

    @Override
    public byte getBlockEntityAction() {
        return BlockHandler.super.getBlockEntityAction();
    }
}
