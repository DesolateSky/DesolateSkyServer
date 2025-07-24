package net.desolatesky.util;

import net.desolatesky.instance.InstancePoint;
import net.desolatesky.player.DSPlayer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.inventory.TransactionOption;
import net.minestom.server.item.ItemStack;

import java.time.Duration;
import java.util.Collection;

public final class InventoryUtil {

    private InventoryUtil() {
        throw new UnsupportedOperationException();
    }

    public static void addItemToInventory(AbstractInventory inventory, ItemStack item, InstancePoint<? extends Point> dropPosition) {
        final ItemStack result = inventory.addItemStack(item, TransactionOption.ALL);
        if (result.isAir()) {
            return;
        }
        final ItemEntity itemEntity = new ItemEntity(result);
        itemEntity.setPickupDelay(Duration.ofMillis(500));
        itemEntity.setInstance(dropPosition.instance(), dropPosition.pos());
    }

    public static void addItemToInventory(DSPlayer player, ItemStack item, InstancePoint<? extends Point> dropPosition) {
        addItemToInventory(player.getInventory(), item, dropPosition);
    }

    public static void addItemsToInventory(AbstractInventory inventory, Collection<ItemStack> items, InstancePoint<? extends Point> dropPosition) {
        for (final ItemStack item : items) {
            addItemToInventory(inventory, item, dropPosition);
        }
    }

    public static void addItemsToInventory(DSPlayer player, Collection<ItemStack> items, InstancePoint<? extends Point> dropPosition) {
        addItemsToInventory(player.getInventory(), items, dropPosition);
    }

}
