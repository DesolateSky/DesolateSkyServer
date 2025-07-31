package net.desolatesky.menu;

import net.desolatesky.menu.action.ClickAction;
import net.desolatesky.menu.action.ClickData;
import net.desolatesky.menu.item.MenuButton;
import net.desolatesky.menu.pattern.Pattern;
import net.desolatesky.player.DSPlayer;
import net.kyori.adventure.text.Component;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.inventory.click.Click;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class GUIMenu implements Menu {

    protected final InventoryType inventoryType;
    protected Component title;
    protected final Map<Integer, MenuButton> menuItems;
    protected final Map<Integer, ClickAction> clickActions;
    protected final @Nullable ClickAction defaultClickAction;
    protected final List<Pattern> patterns;

    protected final Inventory inventory;

    public GUIMenu(InventoryType inventoryType, Component title, Map<Integer, MenuButton> menuItems, Map<Integer, ClickAction> clickActions, List<Pattern> patterns, @Nullable ClickAction defaultClickAction) {
        this.inventoryType = inventoryType;
        this.title = title;
        this.menuItems = menuItems;
        this.clickActions = clickActions;
        this.patterns = patterns;
        this.defaultClickAction = defaultClickAction;
        this.inventory = new Inventory(inventoryType, title);
    }

    @Override
    public Component title() {
        return this.title;
    }

    @Override
    public void setTitle(Component title) {
        this.title = title;
        this.inventory.setTitle(title);
    }

    @Override
    public InventoryType inventoryType() {
        return this.inventoryType;
    }

    @Override
    public ClickResult click(DSPlayer player, Click click) {
        final int slot = click.slot();
        final MenuButton item = this.getItem(slot);
        ClickAction.Result currentResult = null;
        if (item != null) {
            currentResult = this.click(player, click, item.action());
        }
        boolean cancel = currentResult != null && currentResult.cancel();
        if (currentResult == null || currentResult.allowOtherActions()) {
            currentResult = this.click(player, click, this.getClickAction(slot));
        }
        cancel = cancel || (currentResult != null && currentResult.cancel());
        if (currentResult == null || currentResult.allowOtherActions()) {
            currentResult = this.click(player, click, this.defaultClickAction);
        }
        cancel = cancel || (currentResult != null && currentResult.cancel());
        return cancel ? ClickResult.CANCEL : ClickResult.CONTINUE;
    }

    private @Nullable ClickAction.Result click(DSPlayer player, Click click, @Nullable ClickAction clickAction) {
        if (clickAction == null) {
            return null;
        }
        final ClickData clickData = new ClickData(this, player, click.slot(), click, this.inventory.getItemStack(click.slot()));
        return clickAction.onClick(clickData);
    }

    @Override
    public void setItem(int slot, MenuButton item, boolean replace) {
        if (!replace) {
            this.menuItems.putIfAbsent(slot, item);
        } else {
            this.menuItems.put(slot, item);
            this.inventory.setItemStack(slot, item.getItemStack());
        }
    }

    @Override
    public void open(DSPlayer player) {
        this.refresh();
        player.openInventory(this.inventory);
    }

    @Override
    public void refresh() {
        this.inventory.clear();
        this.inventory.setTag(Menu.TAG, this);
        for (final Pattern pattern : this.patterns) {
            pattern.apply(this);
        }
        for (final Map.Entry<Integer, MenuButton> entry : this.menuItems.entrySet()) {
            this.inventory.setItemStack(entry.getKey(), entry.getValue().getItemStack());
        }
    }

    @Override
    public void refresh(int slot) {
        final MenuButton item = this.menuItems.get(slot);
        if (item != null) {
            this.inventory.setItemStack(slot, item.getItemStack());
        }
    }

    @Override
    public MenuButton getItem(int slot) {
        return this.menuItems.getOrDefault(slot, MenuButton.EMPTY);
    }

    @Override
    public @Nullable ClickAction getClickAction(int slot) {
        return this.clickActions.get(slot);
    }

}
