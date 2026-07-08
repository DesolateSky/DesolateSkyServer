package com.fisherl.desolatesky.breaking;

import com.fisherl.desolatesky.player.DSPlayer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;

public interface BreakingManager {

    void tick();

    void startBreaking(DSPlayer player, Point blockPos, Block block);

    void stopBreaking(DSPlayer player, Point blockPos);

}
