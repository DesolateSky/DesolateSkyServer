package net.desolatesky.menu.item;

import net.desolatesky.menu.action.ClickAction;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record SimpleMenuButton(ItemStack itemStack, @Nullable ClickAction action) implements MenuButton {

    public static final SimpleMenuButton EMPTY = new SimpleMenuButton(ItemStack.AIR, null);

    public static SimpleMenuButton menuItem(ItemStack itemStack, boolean cancelClick) {
        return new SimpleMenuButton(itemStack, cancelClick ? ClickAction.CANCEL : null);
    }

    @Override
    public ItemStack getItemStack() {
        return this.itemStack;
    }

}
