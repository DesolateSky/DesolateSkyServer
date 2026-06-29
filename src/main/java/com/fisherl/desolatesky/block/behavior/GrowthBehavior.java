package com.fisherl.desolatesky.block.behavior;

import com.fisherl.desolatesky.block.property.IntBlockProperty;
import com.fisherl.desolatesky.world.DSWorld;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;

public class GrowthBehavior implements RandomTickBehavior {

    private final IntBlockProperty ageProperty;
    private final double growthChance;

    public GrowthBehavior(IntBlockProperty ageProperty, double growthChance) {
        this.ageProperty = ageProperty;
        this.growthChance = growthChance;
    }

    @Override
    public void onRandomTick(DSWorld world, Point pos, Block block, Key blockId) {
        this.ageProperty.read(block)
                .map(num -> num + 1)
                .filter(num -> this.ageProperty.canWrite(block, num))
                .filter(_ -> world.rollChance(pos, this.growthChance))
                .ifPresent(age -> world.setBlock(pos, blockId, b -> this.ageProperty.write(b, age)));
    }
}
