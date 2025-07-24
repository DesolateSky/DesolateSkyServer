package net.desolatesky.crafting.menu.listener;

import net.desolatesky.crafting.CraftingManager;
import net.desolatesky.crafting.menu.CraftingHandler;
import net.desolatesky.crafting.menu.CraftingMenu;
import net.desolatesky.listener.DSListener;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.inventory.InventoryClickEvent;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.trait.InventoryEvent;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.inventory.click.Click;
import net.minestom.server.item.ItemStack;

public final class CraftingMenuListener implements DSListener {

    private final CraftingManager craftingManager;

    public CraftingMenuListener(CraftingManager craftingManager) {
        this.craftingManager = craftingManager;
    }

    @Override
    public void register(EventNode<Event> node) {
        node.addChild(this.craftingMenuClick())
                .addChild(this.playerCraftingMenuClick());
    }

    private EventNode<InventoryEvent> craftingMenuClick() {
        return EventNode.type("crafting-menu-click", EventFilter.INVENTORY, (event, inventory) -> inventory instanceof CraftingMenu)
                .addListener(InventoryPreClickEvent.class, event -> {
                    final CraftingMenu craftingMenu = (CraftingMenu) event.getInventory();
                    this.handlePreClick(event, craftingMenu.craftingHandler());
                })
                .addListener(InventoryClickEvent.class, event -> {
                    final CraftingMenu craftingMenu = (CraftingMenu) event.getInventory();
                    this.handleClick(event, craftingMenu.craftingHandler());
                });
    }

    private EventNode<InventoryEvent> playerCraftingMenuClick() {
        return EventNode.type("player-crafting-menu-click", EventFilter.INVENTORY, (event, inventory) -> inventory instanceof PlayerInventory)
                .addListener(InventoryPreClickEvent.class, event -> {
                    final Player player = event.getPlayer();
                    if (player.getOpenInventory() != null) {
                        return;
                    }
                    final PlayerInventory playerInventory = (PlayerInventory) event.getInventory();
                    final CraftingHandler craftingHandler = createPlayerInventoryCraftingHandler(playerInventory);
                    this.handlePreClick(event, craftingHandler);
                })
                .addListener(InventoryClickEvent.class, event -> {
                    final Player player = event.getPlayer();
                    if (player.getOpenInventory() != null) {
                        return;
                    }
                    final PlayerInventory playerInventory = (PlayerInventory) event.getInventory();
                    final CraftingHandler craftingHandler = createPlayerInventoryCraftingHandler(playerInventory);
                    this.handleClick(event, craftingHandler);
                });
    }

    private static CraftingHandler createPlayerInventoryCraftingHandler(PlayerInventory playerInventory) {
        return new CraftingHandler(playerInventory, 37, 40, 36, 2, 2);
    }

    private void handlePreClick(InventoryPreClickEvent event, CraftingHandler craftingHandler) {
        final int slot = event.getSlot();
        if (!craftingHandler.isCraftingSlot(slot) && !craftingHandler.isOutputSlot(slot)) {
            return;
        }
        if (craftingHandler.isOutputSlot(event.getSlot())) {
            final ItemStack onCursor = event.getPlayer().getInventory().getCursorItem();
            if (!onCursor.isAir() || isShift(event.getClick())) {
                event.setCancelled(true);
            }
        }
    }

    private void handleClick(InventoryClickEvent event, CraftingHandler craftingHandler) {
        final int slot = event.getSlot();
        if (!craftingHandler.isCraftingSlot(slot) && !craftingHandler.isOutputSlot(slot)) {
            return;
        }
        if (craftingHandler.isOutputSlot(event.getSlot())) {
            craftingHandler.collectRecipe(this.craftingManager, event.getClickType());
        } else {
            craftingHandler.fillRecipe(this.craftingManager);
        }
    }

    private static boolean isShift(Click click) {
        return click instanceof Click.LeftShift || click instanceof Click.RightShift;
    }

}
