package net.desolatesky.menu.listener;

import net.desolatesky.listener.DSEventHandler;
import net.desolatesky.listener.DSEventHandlers;
import net.desolatesky.listener.DSListener;
import net.desolatesky.listener.EventHandlerResult;
import net.desolatesky.menu.Menu;
import net.desolatesky.player.DSPlayer;
import net.minestom.server.event.Event;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.trait.InventoryEvent;
import net.minestom.server.inventory.AbstractInventory;

public final class MenuListener implements DSEventHandlers<InventoryEvent> {


    @Override
    public DSListener.Builder<Event> register(DSListener.Builder<Event> builder) {
        return builder.handler(InventoryPreClickEvent.class, this.menuClickHandler());
    }

    private DSEventHandler<InventoryPreClickEvent> menuClickHandler() {
        return event -> {
            final AbstractInventory inventory = event.getInventory();
            final Menu menu = inventory.getTag(Menu.TAG);
            if (menu == null) {
                return EventHandlerResult.CONTINUE_LISTENING;
            }
            final DSPlayer player = (DSPlayer) event.getPlayer();
            final Menu.ClickResult result = menu.click(player, event.getClick());
            if (result == Menu.ClickResult.CANCEL) {
                event.setCancelled(true);
            }
            return switch (result) {
                case CANCEL -> EventHandlerResult.CONSUME_EVENT;
                case CONTINUE -> EventHandlerResult.CONTINUE_LISTENING;
            };
        };
    }

}
