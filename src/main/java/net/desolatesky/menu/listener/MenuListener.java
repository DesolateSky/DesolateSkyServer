package net.desolatesky.menu.listener;

import net.desolatesky.listener.DSEventHandler;
import net.desolatesky.listener.DSEventHandlers;
import net.desolatesky.listener.DSListener;
import net.desolatesky.listener.EventHandlerResult;
import net.desolatesky.menu.Menu;
import net.desolatesky.player.DSPlayer;
import net.minestom.server.event.Event;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.trait.InventoryEvent;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.utils.inventory.PlayerInventoryUtils;

public final class MenuListener implements DSEventHandlers<InventoryEvent> {


    @Override
    public DSListener.Builder<Event> register(DSListener.Builder<Event> builder) {
        return builder.handler(InventoryCloseEvent.class, this.inventoryCloseHandler())
                .handler(InventoryPreClickEvent.class, this.menuClickHandler());
    }

    private DSEventHandler<InventoryCloseEvent> inventoryCloseHandler() {
        return event -> {
            final AbstractInventory inventory = event.getInventory();
            final Menu menu = inventory.getTag(Menu.TAG);
            if (menu == null) {
                return EventHandlerResult.CONTINUE_LISTENING;
            }
            final DSPlayer player = (DSPlayer) event.getPlayer();
            if (!menu.isForPlayer(player)) {
                return EventHandlerResult.CONTINUE_LISTENING;
            }
            menu.onClose();
            return EventHandlerResult.CONSUME_EVENT;
        };
    }

    private DSEventHandler<InventoryPreClickEvent> menuClickHandler() {
        return event -> {
            final AbstractInventory inventory = event.getInventory();
            final Menu menu = inventory.getTag(Menu.TAG);
            final DSPlayer player = (DSPlayer) event.getPlayer();
            if (menu == null) {
                if (!(inventory instanceof final PlayerInventory playerInventory)) {
                    return EventHandlerResult.CONTINUE_LISTENING;
                }
                final AbstractInventory topInventory = player.getOpenInventory();
                if (topInventory == null) {
                    return EventHandlerResult.CONTINUE_LISTENING;
                }
                final Menu topMenu = topInventory.getTag(Menu.TAG);
                if (topMenu == null) {
                    return EventHandlerResult.CONTINUE_LISTENING;
                }
                if (!topMenu.isForPlayer(player)) {
                    event.setCancelled(true);
                    return EventHandlerResult.CONSUME_EVENT;
                }

                final int slot = PlayerInventoryUtils.convertMinestomSlotToPlayerInventorySlot(event.getSlot());
                if (slot == -999) {
                    event.setCancelled(true);
                    return EventHandlerResult.CONSUME_EVENT;
                }
                return click(topMenu, playerInventory, slot, event);
            }
            if (!menu.isForPlayer(player)) {
                event.setCancelled(true);
                return EventHandlerResult.CONSUME_EVENT;
            }
            final int slot = event.getSlot();
            if (slot == -999) {
                event.setCancelled(true);
                return EventHandlerResult.CONSUME_EVENT;
            }
            return click(menu, inventory, slot, event);
        };
    }

    private static EventHandlerResult click(Menu menu, AbstractInventory clickedInventory, int slot, InventoryPreClickEvent event) {
        final Menu.ClickResult result = menu.click(clickedInventory, event.getClick(), slot);
        if (result == Menu.ClickResult.CANCEL) {
            event.setCancelled(true);
        }
        return switch (result) {
            case CANCEL -> EventHandlerResult.CONSUME_EVENT;
            case CONTINUE -> EventHandlerResult.CONTINUE_LISTENING;
        };
    }

}
