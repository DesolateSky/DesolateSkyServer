package net.desolatesky.util;

import net.desolatesky.item.ItemFactory;
import net.desolatesky.player.DSPlayer;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.instance.Instance;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.inventory.TransactionOption;
import net.minestom.server.inventory.click.Click;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.inventory.PlayerInventoryUtils;

import java.time.Duration;
import java.util.Collection;

public final class InventoryUtil {

    private InventoryUtil() {
        throw new UnsupportedOperationException();
    }

    public static void addItemToInventory(AbstractInventory inventory, ItemStack item, Instance instance, Point point) {
        final ItemStack result = inventory.addItemStack(item, TransactionOption.ALL);
        if (result.isAir()) {
            return;
        }
        final ItemEntity itemEntity = new ItemEntity(result);
        itemEntity.setPickupDelay(Duration.ofMillis(500));
        itemEntity.setInstance(instance, point);
    }

    public static void addItemToInventory(DSPlayer player, ItemStack item, Instance instance, Point point) {
        addItemToInventory(player.getInventory(), item, instance, point);
    }

    public static void addItemToInventory(DSPlayer player, ItemStack item) {
        addItemToInventory(player, item, player.getInstance(), player.getPosition());
    }

    public static void addItemToInventory(DSPlayer player, Key itemKey, ItemFactory itemFactory) {
        final ItemStack itemStack = itemFactory.getDefaultItem(itemKey);
        if (itemStack == null) {
            return;
        }
        addItemToInventory(player, itemStack, player.getInstance(), player.getPosition());
    }

    public static void addItemsToInventory(AbstractInventory inventory, Collection<ItemStack> items, Instance instance, Point point) {
        for (final ItemStack item : items) {
            addItemToInventory(inventory, item, instance, point);
        }
    }

    public static void addItemsToInventory(DSPlayer player, Collection<ItemStack> items, Instance instance, Point point) {
        addItemsToInventory(player.getInventory(), items, instance, point);
    }

    public static boolean isLeftClick(Click click) {
        return click instanceof Click.Left || click instanceof Click.LeftShift;
    }

    public static boolean isRightClick(Click click) {
        return click instanceof Click.Right || click instanceof Click.RightShift;
    }

    public static boolean isShiftClick(Click click) {
        return click instanceof Click.LeftShift || click instanceof Click.RightShift;
    }

    public static boolean isShiftClick(ClickType clickType) {
        return clickType == ClickType.SHIFT_CLICK || clickType == ClickType.START_SHIFT_CLICK;
    }

    public static AbstractInventory getClickedInventory(AbstractInventory inventory, PlayerInventory playerInventory, Click click) {
        final int slot = click.slot();
        if (slot < inventory.getSize()) {
            return inventory;
        } else {
            return playerInventory;
        }
    }

    public static ItemStack getClickedItem(AbstractInventory inventory, PlayerInventory playerInventory, Click click) {
        final int slot = click.slot();
        if (slot < inventory.getSize()) {
            return inventory.getItemStack(slot);
        } else {
            final int converted = PlayerInventoryUtils.convertMinestomSlotToPlayerInventorySlot(slot);
            return playerInventory.getItemStack(converted);
        }
    }

    public static boolean subtractFromHeldItem(Player player, PlayerHand hand, int amount) {
        final ItemStack handItem = player.getItemInHand(hand);
        final int itemAmount = handItem.amount();
        if (itemAmount < amount) {
            return false;
        }
        if (itemAmount == amount) {
            player.setItemInHand(hand, ItemStack.AIR);
            return true;
        }
        player.setItemInHand(hand, handItem.withAmount(itemAmount - amount));
        return true;
    }
}
