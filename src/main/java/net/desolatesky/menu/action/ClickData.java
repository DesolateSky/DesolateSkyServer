package net.desolatesky.menu.action;

import net.desolatesky.menu.Menu;
import net.desolatesky.player.DSPlayer;
import net.minestom.server.inventory.click.Click;
import net.minestom.server.item.ItemStack;

public record ClickData(Menu menu, DSPlayer player, int slot, Click click, ItemStack clicked) {

}
