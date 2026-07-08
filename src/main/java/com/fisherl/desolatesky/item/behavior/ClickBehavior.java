package com.fisherl.desolatesky.item.behavior;

import com.fisherl.desolatesky.player.DSPlayer;
import com.fisherl.desolatesky.world.DSWorld;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.Nullable;

public interface ClickBehavior extends ItemBehavior {

    void onClick(DSWorld world, DSPlayer player, Point clickedPos, @Nullable Block clickedBlock);

}
