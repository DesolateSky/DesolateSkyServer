package net.desolatesky.block.behavior.listener;

import net.desolatesky.block.behavior.BlockBehavior;
import net.desolatesky.world.DSWorld;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;

public interface LoadBehavior extends BlockBehavior {

    void save(DSWorld world, Point blockPos, Block block);

    void onLoad(DSWorld world, Point blockPos, Block block);

}
