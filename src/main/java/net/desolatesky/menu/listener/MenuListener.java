package net.desolatesky.menu.listener;

import net.desolatesky.listener.DSListener;
import net.desolatesky.menu.Menu;
import net.desolatesky.player.DSPlayer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.inventory.AbstractInventory;

public final class MenuListener implements DSListener {

    @Override
    public void register(EventNode<Event> node) {
        node.addChild(this.menuClick());
    }

    private EventNode<? extends Event> menuClick() {
        return EventNode.type("menu-click", EventFilter.INVENTORY)
                .addListener(InventoryPreClickEvent.class, event -> {
                    final AbstractInventory inventory = event.getInventory();
                    final Menu menu = inventory.getTag(Menu.TAG);
                    if (menu == null) {
                        return;
                    }
                    final DSPlayer player = (DSPlayer) event.getPlayer();
                    final Menu.ClickResult result = menu.click(player, event.getClick());
                    if (result == Menu.ClickResult.CANCEL) {
                        event.setCancelled(true);
                    }
                });
    }

}
