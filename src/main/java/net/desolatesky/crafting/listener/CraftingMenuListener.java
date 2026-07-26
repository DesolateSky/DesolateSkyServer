package net.desolatesky.crafting.listener;

import net.desolatesky.Listener;
import net.desolatesky.crafting.CraftingHandler;
import net.desolatesky.crafting.CraftingInventory;
import net.desolatesky.crafting.CraftingMenuHolder;
import net.desolatesky.item.ItemFactory;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.recipe.RecipeFactory;
import net.desolatesky.recipe.event.RecipeCraftEvent;
import net.desolatesky.util.InventoryUtil;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.inventory.InventoryClickEvent;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.trait.InventoryEvent;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.inventory.TransactionOption;
import net.minestom.server.inventory.click.Click;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNullByDefault;

@NotNullByDefault
public final class CraftingMenuListener implements Listener<InventoryEvent> {

    private final RecipeFactory recipeFactory;
    private final ItemFactory itemFactory;

    public CraftingMenuListener(RecipeFactory recipeFactory, ItemFactory itemFactory) {
        this.recipeFactory = recipeFactory;
        this.itemFactory = itemFactory;
    }

    @Override
    public void register(EventNode<InventoryEvent> node) {
        this.registerPreClickEventHandler(node);
        this.registerInventoryClickHandler(node);

        node.addListener(InventoryCloseEvent.class, event -> {
           if (!(event.getInventory() instanceof final CraftingInventory craftingInventory)) {
               return;
           }
           final DSPlayer player = (DSPlayer) event.getPlayer();
           for (final ItemStack itemStack : craftingInventory.getItemStacks()) {
               InventoryUtil.addItemToInventory(player, itemStack);
           }
        });
    }

    private void registerPreClickEventHandler(EventNode<InventoryEvent> node) {
        node.addListener(InventoryPreClickEvent.class, event -> {
            final AbstractInventory eventInventory = event.getInventory();
            final PlayerInventory playerInventory = event.getPlayer().getInventory();
            final Click click = event.getClick();
            if (!(event.getPlayer() instanceof final DSPlayer player)) {
                return;
            }
            if (event.getInventory() instanceof PlayerInventory) {
                this.handlePlayerInventoryPreClick(event, player);
                return;
            }
            if (!(InventoryUtil.getClickedInventory(eventInventory, playerInventory, click) instanceof final CraftingInventory craftingInventory)) {
                return;
            }
            final boolean cancel = this.handlePreClick(event, player, craftingInventory.craftingHandler(), click);
            if (cancel) {
                event.setCancelled(true);
            }
        });
    }

    private void registerInventoryClickHandler(EventNode<InventoryEvent> node) {
        node.addListener(InventoryClickEvent.class, event -> {
            if (!(event.getPlayer() instanceof final DSPlayer player)) {
                return;
            }
            if (event.getInventory() instanceof PlayerInventory) {
                this.handlePlayerInventoryClick(event, player);
                return;
            }
            if (!(event.getInventory() instanceof CraftingInventory craftingInventory)) {
                return;
            }
            this.handleClick(event, craftingInventory.craftingHandler());
        });
    }

    private void handlePlayerInventoryPreClick(InventoryPreClickEvent event, DSPlayer player) {
        if (!(player.getOpenInventory() instanceof CraftingInventory)) {
            return;
        }
        final CraftingHandler craftingHandler = createPlayerInventoryCraftingHandler(player);
        final boolean cancel = this.handlePreClick(event, player, craftingHandler, event.getClick());
        if (cancel) {
            event.setCancelled(true);
        }
    }

    private void handlePlayerInventoryClick(InventoryClickEvent event, CraftingMenuHolder holder) {
        final Player player = event.getPlayer();
        if (player.getOpenInventory() != null) {
            return;
        }
        final CraftingHandler craftingHandler = createPlayerInventoryCraftingHandler(holder);
        this.handleClick(event, craftingHandler);
    }

    public static CraftingHandler createPlayerInventoryCraftingHandler(CraftingMenuHolder holder) {
        return new CraftingHandler(holder, 37, 40, 36, 2, 2);
    }

    private static final TransactionOption<ItemStack> TEST_TRANSACTION = (inventory, result, itemChangesMap) -> result;

    private boolean handlePreClick(InventoryPreClickEvent event, DSPlayer player, CraftingHandler craftingHandler, Click click) {
        if (player.getOpenInventory() instanceof CraftingInventory && InventoryUtil.isShiftClick(event.getClick())) {
            return true;
        }
        final int slot = click.slot();
        if (!craftingHandler.isOutputSlot(slot)) {
            return false;
        }
        final PlayerInventory playerInventory = player.getInventory();
        final ItemStack onCursor = playerInventory.getCursorItem();
        if (!onCursor.isSimilar(craftingHandler.getOutputItem()) && !onCursor.isAir() && !InventoryUtil.isShiftClick(click)) {
            return true;
        }
        craftingHandler.collectOutput(this.recipeFactory, this.itemFactory, click, input -> {
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
                    EventDispatcher.call(new RecipeCraftEvent(player, input.recipeId(), amount));
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
                    EventDispatcher.call(new RecipeCraftEvent(player, input.recipeId(), giveAmount));
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
            final int toGive = InventoryUtil.isRightClick(click) ? amountPerCraft : Math.min(canGive, amount * amountPerCraft);
            MinecraftServer.getSchedulerManager().scheduleNextTick(() -> playerInventory.setCursorItem(result.withAmount(cursorItem.amount() + toGive)));
            EventDispatcher.call(new RecipeCraftEvent(player, input.recipeId(), toGive));
            return toGive;
        });
        return true;
    }

    private void handleClick(InventoryClickEvent event, CraftingHandler craftingHandler) {
        final int slot = event.getSlot();
        if (!craftingHandler.isCraftingSlot(slot) && !craftingHandler.isOutputSlot(slot)) {
            return;
        }
        craftingHandler.fillRecipe(this.recipeFactory, this.itemFactory);
    }

    private static boolean isShift(Click click) {
        return click instanceof Click.LeftShift || click instanceof Click.RightShift;
    }

}