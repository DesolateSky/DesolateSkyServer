package net.desolatesky.menu.item;

import net.desolatesky.menu.action.ClickAction;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface MenuButton {

    MenuButton EMPTY = SimpleMenuButton.EMPTY;

    static MenuButton simple(ItemStack itemStack, boolean cancelClick) {
        return SimpleMenuButton.menuItem(itemStack, cancelClick);
    }

    static MenuButton simple(ItemStack itemStack, @Nullable ClickAction action) {
        return new SimpleMenuButton(itemStack, action);
    }

    ItemStack getItemStack();

    @Nullable ClickAction action();

}
