package net.desolatesky.block;

import net.desolatesky.player.DSPlayer;
import net.desolatesky.world.DSWorld;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.event.trait.BlockEvent;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

@NotNullByDefault
public final class HydrateSoilEvent implements InstanceEvent, BlockEvent {

    private final @Nullable DSPlayer player;
    private final DSWorld instance;
    private final Block block;
    private final BlockVec blockPosition;

    public HydrateSoilEvent(@Nullable DSPlayer player, DSWorld instance, Block block, BlockVec blockPosition) {
        this.player = player;
        this.instance = instance;
        this.block = block;
        this.blockPosition = blockPosition;
    }

    public @Nullable DSPlayer player() {
        return this.player;
    }

    @Override
    public Block getBlock() {
        return this.block;
    }

    @Override
    public BlockVec getBlockPosition() {
        return this.blockPosition;
    }

    @Override
    public DSWorld getInstance() {
        return this.instance;
    }
}
