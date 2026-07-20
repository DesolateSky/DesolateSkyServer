package net.desolatesky.item.behavior.impl;

import net.desolatesky.block.MaterialTags;
import net.desolatesky.block.property.BlockProperties;
import net.desolatesky.item.ItemFactory;
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

import java.util.function.Function;

public final class HoeBehavior implements ClickBehavior {

    @Override
    public void onRightClick(DSWorld world, DSPlayer player, PlayerHand hand, ItemStack clickedWith, @Nullable Point clickedPos, @Nullable Block clickedBlock) {
        if (clickedBlock == null || clickedPos == null) {
            return;
        }
        final Material material = clickedBlock.registry().material();
        if (material == null) {
            return;
        }
        if (!MaterialTags.DIRT.contains(material)) {
            return;
        }
        world.setBlock(clickedPos, Block.FARMLAND.key(), Function.identity());
    }

    @Override
    public void onLeftClick(DSWorld world, DSPlayer player, PlayerHand hand, ItemStack clickedWith, @Nullable Point clickedPos, @Nullable Block clickedBlock) {

    }
}
