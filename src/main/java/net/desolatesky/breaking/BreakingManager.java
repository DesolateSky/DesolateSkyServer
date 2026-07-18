package net.desolatesky.breaking;

import net.desolatesky.player.DSPlayer;
import net.desolatesky.world.DSWorld;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;

public interface BreakingManager {

    int UNBREAKABLE_TIME = -1;

    void tick(DSWorld world);

    void startBreaking(DSWorld world, DSPlayer player, Point blockPos, Block block);

    void stopBreaking(DSPlayer player, Point blockPos);

}
