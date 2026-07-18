package net.desolatesky.block.behavior;

import net.desolatesky.world.DSWorld;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;

public abstract class SpreadBehavior implements RandomTickBehavior {

    private final double spreadChance;

    public SpreadBehavior(double spreadChance) {
        this.spreadChance = spreadChance;
    }

    @Override
    public void onRandomTick(DSWorld world, Point pos, Block block, Key blockId) {
        if (!world.rollChance(pos, this.spreadChance)) {
            return;
        }
        this.spreadFrom(world, pos, block, blockId);
    }

    protected abstract void spreadFrom(DSWorld world, Point pos, Block block, Key blockId);

}
