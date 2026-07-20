package net.desolatesky.block.behavior.impl;

import net.desolatesky.block.behavior.BlockDropBehavior;
import net.desolatesky.block.behavior.ClickBehavior;
import net.desolatesky.block.behavior.MiningSpeedBehavior;
import net.desolatesky.block.property.BlockProperties;
import net.desolatesky.item.ItemFactory;
import net.desolatesky.item.ItemTags;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.InventoryUtil;
import net.desolatesky.world.DSWorld;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.MaterialKeys;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class ComposterBehavior implements ClickBehavior, MiningSpeedBehavior, BlockDropBehavior {

    @Override
    public Result onRightClick(
            DSWorld world,
            DSPlayer player,
            PlayerHand hand,
            Point clickedPos,
            Block clickedBlock,
            ItemStack clickedWith
    ) {
        if (BlockProperties.COMPOSTER_LEVEL_PROPERTY.isMax(clickedBlock)) {
            InventoryUtil.addItemToInventory(player, MaterialKeys.DIRT.key(), world.itemFactory());
            world.setBlock(clickedPos, BlockProperties.COMPOSTER_LEVEL_PROPERTY.writeMin(clickedBlock));
            return Result.BLOCK_INTERACTION;
        }
        Integer compostLevel = BlockProperties.COMPOSTER_LEVEL_PROPERTY.read(clickedBlock);
        final Double composterValue = clickedWith.getTag(ItemTags.COMPOSTER_VALUE);
        if (composterValue == null) {
            return Result.ALLOW;
        }
        final double unboxed = composterValue;
        final int intLevel = (int) unboxed;
        final double decimal = unboxed - intLevel;
        if (compostLevel == null) {
            compostLevel = 0;
        }
        compostLevel += intLevel;
        if (world.rollChance(clickedPos, decimal * 100)) {
            compostLevel++;
        }
        compostLevel = Math.min(BlockProperties.COMPOSTER_LEVEL_PROPERTY.max(), compostLevel);
        InventoryUtil.subtractFromHeldItem(player, hand, 1);
        world.setBlock(clickedPos, BlockProperties.COMPOSTER_LEVEL_PROPERTY.write(clickedBlock, compostLevel));
        return null;
    }

    @Override
    public Result onLeftClick(DSWorld world, DSPlayer player, Point clickedPos, Block clickedBlock, ItemStack clickedWith) {
        return Result.ALLOW;
    }

    @Override
    public Collection<ItemStack> getDrops(DSWorld world, Point pos, Block block, Key blockId, ItemFactory itemFactory, @Nullable ItemStack toolUsed) {
        final ItemStack composter = itemFactory.getDefaultItem(MaterialKeys.COMPOSTER.key());
        if (composter == null) {
            return Collections.emptyList();
        }
        return List.of(composter);
    }

    @Override
    public int getTicksToMine(DSWorld world, Point blockPos, Block block, DSPlayer player) {
        return 3 * 20;
    }

    @Override
    public Collection<Type<?>> types() {
        return List.of(Type.CLICK, Type.MINING_SPEED, Type.BLOCK_DROP);
    }
}
