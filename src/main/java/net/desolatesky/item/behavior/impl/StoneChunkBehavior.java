package net.desolatesky.item.behavior.impl;

import net.desolatesky.item.ItemFactory;
import net.desolatesky.item.behavior.ClickBehavior;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.InventoryUtil;
import net.desolatesky.world.DSWorld;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.MaterialKeys;
import org.jetbrains.annotations.Nullable;

public final class StoneChunkBehavior implements ClickBehavior {

    private final double flintChance;

    public StoneChunkBehavior(double flintChance) {
        this.flintChance = flintChance;
    }

    @Override
    public void onRightClick(
            DSWorld world,
            DSPlayer player,
            PlayerHand hand,
            ItemStack clickedWith,
            @Nullable Point clickedPos,
            @Nullable Block clickedBlock
    ) {

    }

    @Override
    public void onLeftClick(
            DSWorld world,
            DSPlayer player,
            PlayerHand hand,
            ItemStack clickedWith,
            @Nullable Point clickedPos,
            @Nullable Block clickedBlock
    ) {
        if (clickedPos == null || clickedBlock == null || clickedBlock.isAir()) {
            return;
        }
        if (!world.rollChance(clickedPos, this.flintChance)) {
            return;
        }
        InventoryUtil.subtractFromHeldItem(player, hand, 1);
        InventoryUtil.addItemToInventory(player, MaterialKeys.FLINT.key(), world.itemFactory());
    }
}
