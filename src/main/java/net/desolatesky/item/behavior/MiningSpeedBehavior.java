package net.desolatesky.item.behavior;

import net.desolatesky.player.DSPlayer;
import net.desolatesky.world.DSWorld;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;

public interface MiningSpeedBehavior extends ItemBehavior {

    int modifyTickSpeed(
            int originalSpeed,
            DSWorld world,
            DSPlayer player,
            ItemStack minedWith,
            Point blockPos,
            Block block
    );
}
