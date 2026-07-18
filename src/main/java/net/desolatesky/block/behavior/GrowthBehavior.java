package net.desolatesky.block.behavior;

import net.desolatesky.block.property.IntBlockProperty;
import net.desolatesky.util.BlockUtil;
import net.desolatesky.world.DSWorld;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;

public abstract class GrowthBehavior implements RandomTickBehavior {

    protected final IntBlockProperty ageProperty;
    protected final double growthChance;

    public GrowthBehavior(IntBlockProperty ageProperty, double growthChance) {
        this.ageProperty = ageProperty;
        this.growthChance = growthChance;
    }

    @Override
    public void onRandomTick(DSWorld world, Point pos, Block block, Key blockId) {
        final Integer age = this.ageProperty.read(block);
        if (age == null) {
            return;
        }
        final int nextAge = age + 1;
        if (!this.ageProperty.canWrite(block, nextAge)) {
            return;
        }
        if (!this.canGrow(world, pos, block, blockId)) {
            return;
        }
        if (!world.rollChance(pos, this.growthChance)) {
            return;
        }
        final Block newBlock = this.ageProperty.write(block, nextAge);
        world.setBlock(pos, newBlock);
        if (nextAge == this.ageProperty.max()) {
            this.onMaxGrow(world, pos, newBlock, blockId);
        }
    }

    protected abstract boolean canGrow(DSWorld world, Point pos, Block block, Key blockId);

    protected abstract void onMaxGrow(DSWorld world, Point pos, Block block, Key blockId);
}
