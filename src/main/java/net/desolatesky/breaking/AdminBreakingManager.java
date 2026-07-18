package net.desolatesky.breaking;

import net.desolatesky.permission.Permission;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.world.DSWorld;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;

public final class AdminBreakingManager implements BreakingManager {

    @Override
    public void tick(DSWorld world) {

    }

    @Override
    public void startBreaking(DSWorld world, DSPlayer player, Point blockPos, Block block) {
        if (player.hasPermission(Permission.ADMIN)) {
            world.setBlock(blockPos, Block.AIR, false);
        }
    }

    @Override
    public void stopBreaking(DSPlayer player, Point blockPos) {

    }
}
