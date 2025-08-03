package net.desolatesky.menu;

import net.desolatesky.menu.action.ClickAction;
import net.desolatesky.menu.item.MenuButton;
import net.desolatesky.menu.pattern.Pattern;
import net.desolatesky.player.DSPlayer;
import net.kyori.adventure.text.Component;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.inventory.click.Click;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Menu {

    Tag<Menu> TAG = Tag.Transient("menu");

    Component title();

    void setTitle(Component title);

    InventoryType inventoryType();

    ClickResult click(AbstractInventory clickedInventory, Click click, int slot);

    void setItem(int slot, MenuButton item, boolean replace);

    MenuButton getItem(int slot);

    @Nullable ClickAction getClickAction(int slot);

    void refresh();

    void refresh(int slot);

    void open();

    DSPlayer player();

    void onClose();

    default boolean isForPlayer(DSPlayer player) {
        return this.player().equals(player);
    }

    enum ClickResult {

        CONTINUE,
        CANCEL

    }

    static Builder builder(Component title, InventoryType inventoryType) {
        return new Builder(title, inventoryType);
    }

    class Builder {

        protected final Component title;
        protected final InventoryType inventoryType;
        protected final Map<Integer, MenuButton> items;
        protected final Map<Integer, ClickAction> clickActions;
        protected final List<Pattern> patterns;
        protected @Nullable ClickAction defaultClickAction;

        protected Builder(Component title, InventoryType inventoryType) {
            this.title = title;
            this.inventoryType = inventoryType;
            this.items = new HashMap<>();
            this.clickActions = new HashMap<>();
            this.patterns = new ArrayList<>();
            this.defaultClickAction = null;
        }

        public Builder item(int slot, MenuButton item) {
            this.items.put(slot, item);
            return this;
        }

        public Builder items(Map<Integer, ? extends MenuButton> items) {
            this.items.putAll(items);
            return this;
        }

        public Builder items(Collection<Integer> slots, MenuButton item) {
            for (final Integer slot : slots) {
                this.items.put(slot, item);
            }
            return this;
        }

        public Builder clickAction(int slot, ClickAction action) {
            this.clickActions.put(slot, action);
            return this;
        }

        public Builder clickActions(Map<Integer, ClickAction> actions) {
            this.clickActions.putAll(actions);
            return this;
        }

        public Builder clickActions(Collection<Integer> slots, ClickAction action) {
            for (final Integer slot : slots) {
                this.clickActions.put(slot, action);
            }
            return this;
        }

        public Builder pattern(Pattern pattern) {
            this.patterns.add(pattern);
            return this;
        }

        public Builder patterns(Collection<Pattern> patterns) {
            this.patterns.addAll(patterns);
            return this;
        }

        public Builder defaultClickAction(ClickAction action) {
            this.defaultClickAction = action;
            return this;
        }

        public Menu build(DSPlayer player) {
            return new GUIMenu(player, this.inventoryType, this.title, this.items, this.clickActions, this.patterns, this.defaultClickAction);
        }

    }

}
