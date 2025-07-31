package net.desolatesky.crafting.menu.listener;

import net.desolatesky.crafting.CraftingManager;
import net.desolatesky.crafting.menu.CraftingHandler;
import net.desolatesky.crafting.menu.CraftingMenu;
import net.desolatesky.listener.DSEventHandler;
import net.desolatesky.listener.DSEventHandlers;
import net.desolatesky.listener.DSListener;
import net.desolatesky.listener.EventHandlerResult;
import net.desolatesky.util.InventoryUtil;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.inventory.InventoryClickEvent;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.trait.InventoryEvent;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.inventory.TransactionOption;
import net.minestom.server.inventory.click.Click;
import net.minestom.server.item.ItemStack;

public final class CraftingMenuListener implements DSEventHandlers<InventoryEvent> {

    private final CraftingManager craftingManager;

    public CraftingMenuListener(CraftingManager craftingManager) {
        this.craftingManager = craftingManager;
    }

    @Override
    public DSListener.Builder<Event> register(DSListener.Builder<Event> builder) {
        return builder.handler(InventoryPreClickEvent.class, this.preClickEventHandler())
                .handler(InventoryClickEvent.class, this.inventoryClickHandler());
    }

    private DSEventHandler<InventoryPreClickEvent> preClickEventHandler() {
        return event -> {
            final AbstractInventory eventInventory = event.getInventory();
            final PlayerInventory playerInventory = event.getPlayer().getInventory();
            final Click click = event.getClick();
            if (event.getInventory() instanceof final PlayerInventory eventPlayerInventory) {
                return this.handlePlayerInventoryPreClick(event, eventPlayerInventory);
            }
            if (!(InventoryUtil.getClickedInventory(eventInventory, playerInventory, click) instanceof final CraftingMenu craftingMenu)) {
                return EventHandlerResult.CONSUME_EVENT;
            }
            final boolean cancel = this.handlePreClick(event.getPlayer(), playerInventory, craftingMenu, craftingMenu.craftingHandler(), click);
            if (cancel) {
                event.setCancelled(true);
                return EventHandlerResult.CONSUME_EVENT;
            }
            return EventHandlerResult.CONSUME_EVENT;
        };
    }

    private DSEventHandler<InventoryClickEvent> inventoryClickHandler() {
        return event -> {
            if (event.getInventory() instanceof final PlayerInventory playerInventory) {
                return this.handlePlayerInventoryClick(event, playerInventory);
            }
            if (!(event.getInventory() instanceof CraftingMenu craftingMenu)) {
                return EventHandlerResult.CONSUME_EVENT;
            }
            this.handleClick(event, craftingMenu.craftingHandler());
            return EventHandlerResult.CONSUME_EVENT;
        };
    }

    private EventHandlerResult handlePlayerInventoryPreClick(InventoryPreClickEvent event, PlayerInventory playerInventory) {
        final Player player = event.getPlayer();
        if (player.getOpenInventory() != null) {
            return EventHandlerResult.CONTINUE_LISTENING;
        }
        final CraftingHandler craftingHandler = createPlayerInventoryCraftingHandler(playerInventory);
        final boolean cancel = this.handlePreClick(player, playerInventory, playerInventory, craftingHandler, event.getClick());
        if (cancel) {
            event.setCancelled(true);
            return EventHandlerResult.CONSUME_EVENT;
        }
        return EventHandlerResult.CONSUME_EVENT;
    }

    private EventHandlerResult handlePlayerInventoryClick(InventoryClickEvent event, PlayerInventory playerInventory) {
        final Player player = event.getPlayer();
        if (player.getOpenInventory() != null) {
            return EventHandlerResult.CONTINUE_LISTENING;
        }
        final CraftingHandler craftingHandler = createPlayerInventoryCraftingHandler(playerInventory);
        this.handleClick(event, craftingHandler);
        return EventHandlerResult.CONSUME_EVENT;
    }

    private static CraftingHandler createPlayerInventoryCraftingHandler(PlayerInventory playerInventory) {
        return new CraftingHandler(playerInventory, 37, 40, 36, 2, 2);
    }

    private static final TransactionOption<ItemStack> TEST_TRANSACTION = (inventory, result, itemChangesMap) -> result;

    private boolean handlePreClick(Player player, PlayerInventory playerInventory, AbstractInventory clickedInventory, CraftingHandler craftingHandler, Click click) {
        final int slot = click.slot();
        if (!craftingHandler.isOutputSlot(slot)) {
            return false;
        }
        final ItemStack onCursor = playerInventory.getCursorItem();
        if (!onCursor.isSimilar(craftingHandler.getOutputItem()) && !onCursor.isAir() && !InventoryUtil.isShiftClick(click)) {
            return true;
        }
        craftingHandler.collectOutput(this.craftingManager, clickedInventory, click, input -> {
            final int matches = input.totalMatches();
            final int amountPerCraft = input.amountPerCraft();
            final int amount = matches * amountPerCraft;
            if (amount <= 0) {
                return 0;
            }
            final ItemStack result = input.resultItem().withAmount(amount);
            if (result.isAir()) {
                return 0;
            }
            final boolean shiftClick = InventoryUtil.isShiftClick(click);
            if (shiftClick) {
                final ItemStack leftOver = playerInventory.addItemStack(result, TEST_TRANSACTION);
                if (leftOver.isAir() || leftOver.amount() == 0) {
                    playerInventory.addItemStack(result);
                    return matches;
                } else {
                    int giveAmount = amount - leftOver.amount();
                    final int matchesToGive = giveAmount / amountPerCraft;
                    giveAmount = matchesToGive * amountPerCraft;
                    if (giveAmount <= 0) {
                        return 0;
                    }
                    playerInventory.addItemStack(result.withAmount(giveAmount));
                    return matchesToGive;
                }
            }
            final ItemStack cursorItem = playerInventory.getCursorItem();
            if (!cursorItem.isSimilar(result) && !cursorItem.isAir()) {
                return 0;
            }
            final int canGive = cursorItem.maxStackSize() - cursorItem.amount();
            if (canGive <= 0) {
                return 0;
            }
            // 1 only if not shift-clicking
            if (canGive < amountPerCraft) {
                return 0;
            }
            MinecraftServer.getSchedulerManager().scheduleNextTick(() -> playerInventory.setCursorItem(result.withAmount(cursorItem.amount() + amountPerCraft)));
            return amountPerCraft;
        });
        return true;
    }

    private void handleClick(InventoryClickEvent event, CraftingHandler craftingHandler) {
        final int slot = event.getSlot();
        if (!craftingHandler.isCraftingSlot(slot) && !craftingHandler.isOutputSlot(slot)) {
            return;
        }
        craftingHandler.fillRecipe(this.craftingManager);
    }

    private static boolean isShift(Click click) {
        return click instanceof Click.LeftShift || click instanceof Click.RightShift;
    }

}
