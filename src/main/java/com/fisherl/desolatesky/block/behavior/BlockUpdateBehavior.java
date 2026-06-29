package com.fisherl.desolatesky.block.behavior;

import com.fisherl.desolatesky.world.DSWorld;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;

import java.util.Set;

public interface BlockUpdateBehavior extends BlockBehavior {

    /**
     *
     * @param world
     * @param blockPos
     * @param block
     * @return true if the block was changed as a result of this call
     */
    boolean update(DSWorld world, Point blockPos, Block block);

    Set<Point> getBlocksToUpdate(DSWorld world, Point blockPos, Block block);


}
