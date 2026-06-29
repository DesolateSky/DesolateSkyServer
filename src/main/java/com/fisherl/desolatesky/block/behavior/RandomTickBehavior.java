package com.fisherl.desolatesky.block.behavior;

import com.fisherl.desolatesky.world.DSWorld;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;

public interface RandomTickBehavior extends BlockBehavior {

    void onRandomTick(DSWorld world, Point pos, Block block, Key blockId);

}
