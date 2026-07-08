package com.fisherl.desolatesky.item.behavior;

import com.fisherl.desolatesky.player.DSPlayer;
import com.fisherl.desolatesky.world.DSWorld;
import net.minestom.server.coordinate.Point;
import net.minestom.server.item.ItemStack;

public interface BlockPlaceBehavior extends ItemBehavior {

    BlockPlaceBehavior BLOCKED = (_, _, _, _) -> false;

    boolean canPlace(DSWorld world, DSPlayer player, ItemStack itemStack, Point blockPos);

}
