package net.desolatesky.item.tool.menu;

import net.desolatesky.item.DSItem;
import net.desolatesky.item.DSItemRegistry;
import net.desolatesky.item.ItemTags;
import net.desolatesky.item.tool.Tool;
import net.desolatesky.item.tool.part.ToolPart;
import net.desolatesky.item.tool.part.ToolPartType;
import net.desolatesky.item.tool.registry.ToolPartRegistry;
import net.desolatesky.menu.GUIMenu;
import net.desolatesky.menu.action.ClickAction;
import net.desolatesky.menu.action.ClickData;
import net.desolatesky.menu.item.MenuButton;
import net.desolatesky.menu.item.SimpleMenuButton;
import net.desolatesky.menu.pattern.Pattern;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.InventoryUtil;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ToolMenu extends GUIMenu {

    public static final int TOOL_STATION_HEAD_PART_SLOT = 11;
    private static final List<Integer> TOOL_STATION_OTHER_PART_SLOTS = List.of(13, 15);
    private static final int TOOL_STATION_RESULT_SLOT = 31;

    private final DSItemRegistry itemRegistry;
    private final ToolPartRegistry toolPartRegistry;
    private final MenuButton fillButton;

    private final int headPartSlot;
    private final List<Integer> otherPartSlots;
    private final int resultSlot;

    private @Nullable ItemStack headPartItem = null;
    private @Nullable ItemStack craftedItem = null;
    private final Map<Integer, ItemStack> otherParts;

    private ToolMenu(
            DSPlayer player,
            InventoryType inventoryType,
            Component title,
            Map<Integer, MenuButton> menuItems,
            Map<Integer, ClickAction> clickActions,
            List<Pattern> patterns,
            @Nullable ClickAction defaultClickAction,
            MenuButton fillButton,
            DSItemRegistry itemRegistry,
            ToolPartRegistry toolPartRegistry,
            int headPartSlot,
            List<Integer> otherPartSlots,
            int resultSlot
    ) {
        super(player, inventoryType, title, menuItems, clickActions, patterns, defaultClickAction);
        this.fillButton = fillButton;
        this.itemRegistry = itemRegistry;
        this.toolPartRegistry = toolPartRegistry;
        this.headPartSlot = headPartSlot;
        this.otherPartSlots = otherPartSlots;
        this.resultSlot = resultSlot;
        this.otherParts = new HashMap<>();
    }

    public static ToolMenu createToolStationMenu(ItemStack fillItem, DSItemRegistry itemRegistry, ToolPartRegistry registry, DSPlayer player) {
        final Map<Integer, MenuButton> menuItems = new HashMap<>();
        final Map<Integer, ClickAction> clickActions = new HashMap<>();
        TOOL_STATION_OTHER_PART_SLOTS.forEach(slot -> clickActions.put(slot, ToolMenu::defaultClickAction));
        final MenuButton fillButton = new SimpleMenuButton(fillItem, click -> ClickAction.Result.CANCEL_AND_ALLOW_OTHER_ACTIONS);
        return new ToolMenu(
                player,
                InventoryType.CHEST_5_ROW,
                Component.text("Tool Menu"),
                menuItems,
               clickActions,
                List.of(Pattern.fill(fillButton, Set.of(TOOL_STATION_HEAD_PART_SLOT))),
                ToolMenu::defaultClickAction,
                fillButton,
                itemRegistry,
                registry,
                TOOL_STATION_HEAD_PART_SLOT,
                TOOL_STATION_OTHER_PART_SLOTS,
                TOOL_STATION_RESULT_SLOT
        );
    }

    private static ClickAction.Result defaultClickAction(ClickData click) {
        if (!(click.menu() instanceof ToolMenu menu)) {
            return ClickAction.Result.CANCEL;
        }
        final AbstractInventory clickedInventory = click.clickedInventory();
        if (clickedInventory instanceof final PlayerInventory playerInventory) {
            return menu.handlePlayerInventoryClick(click, playerInventory);
        }
        return menu.handleTopMenuClick(click, clickedInventory);
    }

    private ClickAction.Result handleTopMenuClick(ClickData clickData, AbstractInventory abstractInventory) {
        final int slot = clickData.slot();
        final ItemStack otherPart = this.otherParts.remove(slot);
        if (otherPart != null) {
            abstractInventory.setItemStack(slot, ItemStack.AIR);
            InventoryUtil.addItemToInventory(this.player, otherPart);
            this.fillExtraSlots();
            return ClickAction.Result.CANCEL;
        }
        if (slot == this.headPartSlot) {
            if (this.headPartItem != null) {
                abstractInventory.setItemStack(slot, ItemStack.AIR);
                InventoryUtil.addItemToInventory(this.player, this.headPartItem);
                this.headPartItem = null;
                this.closeExtraSlots(false);
                this.checkResult();
            }
            return ClickAction.Result.CANCEL;
        }
        if (slot == this.resultSlot) {
            if (this.craftedItem != null) {
                abstractInventory.setItemStack(slot, ItemStack.AIR);
                InventoryUtil.addItemToInventory(this.player, this.craftedItem);
                this.consumeAllCraftingSlots();
            }
            return ClickAction.Result.CANCEL;
        }
        return ClickAction.Result.CANCEL;
    }

    private ClickAction.Result handlePlayerInventoryClick(ClickData clickData, PlayerInventory playerInventory) {
        final int slot = clickData.slot();
        final ItemStack clicked = playerInventory.getItemStack(clickData.slot());
        final ToolPart clickedPart = this.toolPartRegistry.getPartFromItem(clicked);
        if (clickedPart == null) {
            return ClickAction.Result.CANCEL;
        }
        if (this.headPartItem == null) {
            return this.handleHeadPartClick(clickData, playerInventory, slot, clicked, clickedPart);
        }
        if (clickedPart.type().isHead()) {
            return ClickAction.Result.CANCEL;
        }
        if (this.otherParts.size() >= this.otherPartSlots.size()) {
            return ClickAction.Result.CANCEL;
        }
        playerInventory.setItemStack(slot, ItemStack.AIR);
        final int inventorySlot = this.otherPartSlots.get(this.otherParts.size());
        this.otherParts.put(inventorySlot, clicked);
        this.fillExtraSlots();
        this.checkResult();
        return ClickAction.Result.CANCEL;
    }

    private ClickAction.Result handleHeadPartClick(ClickData clickData, PlayerInventory playerInventory, int slot, ItemStack clicked, ToolPart toolPart) {
        if (!toolPart.type().isHead()) {
            return ClickAction.Result.CANCEL;
        }
        if (this.headPartItem == null) {
            this.headPartItem = clicked;
            playerInventory.setItemStack(slot, ItemStack.AIR);
            this.inventory.setItemStack(this.headPartSlot, clicked);
            this.openExtraSlots();
        }
        return ClickAction.Result.CANCEL;
    }

    private void fillExtraSlots() {
        this.otherParts.forEach(this.inventory::setItemStack);
    }

    private void openExtraSlots() {
        this.otherPartSlots.forEach(otherPartSlot -> this.inventory.setItemStack(otherPartSlot, ItemStack.AIR));
    }

    private void closeExtraSlots(boolean clearOtherParts) {
        this.otherParts.entrySet().removeIf(entry -> {
            final int slot = entry.getKey();
            final ItemStack item = entry.getValue();
            if (item == null || item.isAir()) {
                return true;
            }
            if (clearOtherParts) {
                return true;
            }
            this.inventory.setItemStack(slot, ItemStack.AIR);
            InventoryUtil.addItemToInventory(this.player, item);
            return true;
        });
        this.inventory.setItemStack(this.resultSlot, ItemStack.AIR);
        this.otherPartSlots.forEach(otherPartSlot -> this.setItem(otherPartSlot, this.fillButton, true));
    }

    private void consumeAllCraftingSlots() {
        this.closeExtraSlots(true);
        this.headPartItem = null;
        this.craftedItem = null;
        this.inventory.setItemStack(this.headPartSlot, ItemStack.AIR);
        this.inventory.setItemStack(this.resultSlot, ItemStack.AIR);
    }

    private void checkResult() {
        if (this.headPartItem == null) {
            this.inventory.setItemStack(this.resultSlot, ItemStack.AIR);
            return;
        }
        final ToolPart headToolPart = this.toolPartRegistry.getPartFromItem(this.headPartItem);
        if (headToolPart == null) {
            return;
        }
        final Map<ToolPartType, Integer> partCounts = new HashMap<>();
        this.otherParts.values().forEach(part -> {
            final ToolPart toolPart = this.toolPartRegistry.getPartFromItem(part);
            if (toolPart != null) {
                partCounts.merge(toolPart.type(), 1, Integer::sum);
            }
        });
        final boolean canCraft = headToolPart.type().meetsRequirements(partCounts);
        if (!canCraft) {
            return;
        }
        final Key craftedItemKey = headToolPart.type().craftsIntoItem();
        if (craftedItemKey == null) {
            return;
        }
        final DSItem craftedItem = this.itemRegistry.getItem(craftedItemKey);
        if (craftedItem == null) {
            return;
        }
        final List<Key> toolPartKeys = new ArrayList<>();
        toolPartKeys.add(DSItem.getIdFor(this.headPartItem));
        for (final ItemStack partItem : this.otherParts.values()) {
            toolPartKeys.add(DSItem.getIdFor(partItem));
        }
        final Tool tool = new Tool(craftedItemKey, toolPartKeys);
        this.craftedItem = tool.createItemStack(this.itemRegistry, this.toolPartRegistry);
        this.inventory.setItemStack(this.resultSlot, this.craftedItem);
    }

    @Override
    public void onClose() {
        if (this.headPartItem != null) {
            InventoryUtil.addItemToInventory(this.player, this.headPartItem);
            this.headPartItem = null;
        }
        for (final ItemStack part : this.otherParts.values()) {
            InventoryUtil.addItemToInventory(this.player, part);
        }
        this.otherParts.clear();
    }
}
