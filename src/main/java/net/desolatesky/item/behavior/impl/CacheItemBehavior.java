package net.desolatesky.item.behavior.impl;

import net.desolatesky.item.ItemFactory;
import net.desolatesky.item.ItemTags;
import net.desolatesky.item.behavior.ClickBehavior;
import net.desolatesky.item.definition.ItemDefinition;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.InventoryUtil;
import net.desolatesky.util.Pair;
import net.desolatesky.world.DSWorld;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public final class CacheItemBehavior implements ClickBehavior {

    @Override
    public void onRightClick(DSWorld world, DSPlayer player, PlayerHand hand, ItemStack clickedWith, @Nullable Point clickedPos, @Nullable Block clickedBlock) {
        final List<Pair<Key, Integer>> itemIds = clickedWith.getTag(ItemTags.CACHE_ITEMS_KEY);
        if (itemIds == null) {
            return;
        }
        if (!InventoryUtil.subtractFromHeldItem(player, hand, 1)) {
            return;
        }
        final ItemFactory itemFactory = world.itemFactory();
        final List<ItemStack> itemStacks = itemIds.stream()
                .map(p -> new Pair<>(itemFactory.getItemDefinition(p.first()), p.second()))
                .filter(p -> Objects.nonNull(p.first()))
                .map(p -> p.first().defaultItemStack().withAmount(p.second()))
                .toList();
        InventoryUtil.addItemsToInventory(player, itemStacks, player.getInstance(), player.getPosition());
    }

    @Override
    public void onLeftClick(DSWorld world, DSPlayer player, PlayerHand hand, ItemStack clickedWith, @Nullable Point clickedPos, @Nullable Block clickedBlock) {

    }
}
