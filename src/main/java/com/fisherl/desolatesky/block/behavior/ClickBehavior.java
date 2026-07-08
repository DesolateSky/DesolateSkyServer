package com.fisherl.desolatesky.block.behavior;

import com.fisherl.desolatesky.player.DSPlayer;
import com.fisherl.desolatesky.world.DSWorld;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;

public interface ClickBehavior extends BlockBehavior {

    ClickBehavior.Result onRightClick(DSWorld world, DSPlayer player, PlayerHand hand, Point clickedPos, Block clickedBlock, ItemStack clickedWith);

    ClickBehavior.Result onLeftClick(DSWorld world, DSPlayer player, Point clickedPos, Block clickedBlock, ItemStack clickedWith);

    enum Result {
        BLOCK_INTERACTION,
        ALLOW
    }

}
