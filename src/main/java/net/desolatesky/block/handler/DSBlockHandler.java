package net.desolatesky.block.handler;

import net.desolatesky.block.BlockFactory;
import net.desolatesky.block.behavior.BlockBehavior;
import net.desolatesky.block.behavior.TickBehavior;
import net.desolatesky.block.definition.BlockDefinition;
import net.desolatesky.util.BlockUtil;
import net.desolatesky.world.DSWorld;
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
        final Block block = tick.getBlock();
        final Point pos = tick.getBlockPosition();
        final Key blockId = BlockUtil.getBlockId(block);
        final BlockDefinition blockDefinition = this.blockFactory.getBlockDefinition(blockId);
        if (blockDefinition == null) {
            return;
        }
        final TickBehavior tickBehavior = blockDefinition.getBehavior(BlockBehavior.Type.TICK);
        if (tickBehavior == null) {
            return;
        }
        if (!(tick.getInstance() instanceof final DSWorld world)) {
            return;
        }
        tickBehavior.onTick(world, pos, block, blockId);
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
