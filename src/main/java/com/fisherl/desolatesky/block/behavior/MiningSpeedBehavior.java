package com.fisherl.desolatesky.block.behavior;

import com.fisherl.desolatesky.player.DSPlayer;
import com.fisherl.desolatesky.world.DSWorld;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;

public interface MiningSpeedBehavior extends BlockBehavior {

    int getTicksToMine(DSWorld world, Point blockPos, Block block, DSPlayer player);

    MiningSpeedBehavior UNBREAKABLE = (_, _, _, _) -> -1;

}
