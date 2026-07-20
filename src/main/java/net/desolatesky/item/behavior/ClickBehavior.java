package net.desolatesky.item.behavior;

import net.desolatesky.player.DSPlayer;
import net.desolatesky.world.DSWorld;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface ClickBehavior extends ItemBehavior {

    void onRightClick(
            DSWorld world,
            DSPlayer player,
            PlayerHand hand,
            ItemStack clickedWith,
            @Nullable Point clickedPos,
            @Nullable Block clickedBlock
    );

    void onLeftClick(
            DSWorld world,
            DSPlayer player,
            PlayerHand hand,
            ItemStack clickedWith,
            @Nullable Point clickedPos,
            @Nullable Block clickedBlock
    );
}
