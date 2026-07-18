package net.desolatesky.block.behavior.impl;

import net.desolatesky.block.behavior.BlockBehavior;
import net.desolatesky.block.behavior.BlockDropBehavior;
import net.desolatesky.block.behavior.ClickBehavior;
import net.desolatesky.block.behavior.MiningSpeedBehavior;
import net.desolatesky.crafting.CraftingInventory;
import net.desolatesky.item.ItemFactory;
import net.desolatesky.item.definition.ItemDefinition;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.world.DSWorld;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class CraftingTableBehavior implements ClickBehavior, MiningSpeedBehavior, BlockDropBehavior {

    @Override
    public Result onRightClick(DSWorld world, DSPlayer player, PlayerHand hand, Point clickedPos, Block clickedBlock, ItemStack clickedWith) {
        final CraftingInventory craftingInventory = new CraftingInventory(world, clickedPos);
        player.openInventory(craftingInventory);
        return Result.BLOCK_INTERACTION;
    }

    @Override
    public Result onLeftClick(DSWorld world, DSPlayer player, Point clickedPos, Block clickedBlock, ItemStack clickedWith) {
        return Result.ALLOW;
    }

    @Override
    public Collection<ItemStack> getDrops(DSWorld world,
                                          Point pos,
                                          Block block,
                                          Key blockId,
                                          ItemFactory itemFactory,
                                          @Nullable ItemStack toolUsed) {
        final ItemDefinition itemDefinition = itemFactory.getItemDefinition(Material.CRAFTING_TABLE.key());
        if (itemDefinition == null) {
            return Collections.emptyList();
        }
        return List.of(itemDefinition.defaultItemStack());
    }

    @Override
    public int getTicksToMine(DSWorld world, Point blockPos, Block block, DSPlayer player) {
        return 2 * 20;
    }

    @Override
    public Collection<Type<?>> types() {
        return List.of(Type.MINING_SPEED, Type.BLOCK_DROP, Type.CLICK);
    }
}
