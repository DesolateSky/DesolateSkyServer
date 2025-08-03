package net.desolatesky.menu.action;

import net.desolatesky.menu.Menu;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.inventory.click.Click;
import net.minestom.server.item.ItemStack;

public record ClickData(Menu menu, AbstractInventory clickedInventory, int slot, Click click, ItemStack clicked) {

}
