package net.desolatesky.item.behavior.impl;

import net.desolatesky.block.MCMaterialTags;
import net.desolatesky.item.behavior.ClickBehavior;
import net.desolatesky.player.DSPlayer;
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
        final Material material = clickedBlock.material();
        if (material == null) {
            return;
        }
        if (!MCMaterialTags.DIRT.contains(material) && !MCMaterialTags.GRASS_BLOCKS.contains(material)) {
            return;
        }
        world.setBlock(clickedPos, Block.FARMLAND.key(), Function.identity());
    }

    @Override
    public void onLeftClick(DSWorld world, DSPlayer player, PlayerHand hand, ItemStack clickedWith, @Nullable Point clickedPos, @Nullable Block clickedBlock) {

    }
}
