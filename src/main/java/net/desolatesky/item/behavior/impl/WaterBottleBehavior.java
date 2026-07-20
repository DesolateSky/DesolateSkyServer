package net.desolatesky.item.behavior.impl;

import net.desolatesky.block.property.BlockProperties;
import net.desolatesky.item.behavior.ClickBehavior;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.BlockUtil;
import net.desolatesky.util.InventoryUtil;
import net.desolatesky.world.DSWorld;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Nullable;

public final class WaterBottleBehavior implements ClickBehavior {

    @Override
    public void onRightClick(DSWorld world, DSPlayer player, PlayerHand hand, ItemStack clickedWith, @Nullable Point clickedPos, @Nullable Block clickedBlock) {
        if (clickedBlock == null || clickedPos == null) {
            return;
        }
        if (!BlockUtil.getBlockId(clickedBlock).equals(Block.FARMLAND.key())) {
            return;
        }
        final Integer moisture = BlockProperties.FARMLAND_MOISTURE_PROPERTY.read(clickedBlock);
        final int maxMoisture = BlockProperties.FARMLAND_MOISTURE_PROPERTY.max();
        if (moisture == null || moisture >= maxMoisture) {
            return;
        }
        world.setBlock(clickedPos, BlockProperties.FARMLAND_MOISTURE_PROPERTY.write(clickedBlock, maxMoisture));
        InventoryUtil.subtractFromHeldItem(player, hand, 1);
        final ItemStack glassBottle = world.itemFactory().getDefaultItem(Material.GLASS_BOTTLE.key());
        if (glassBottle == null) {
            return;
        }
        InventoryUtil.addItemToInventory(player, glassBottle, world, player.getPosition());
    }

    @Override
    public void onLeftClick(DSWorld world, DSPlayer player, PlayerHand hand, ItemStack clickedWith, @Nullable Point clickedPos, @Nullable Block clickedBlock) {

    }
}
