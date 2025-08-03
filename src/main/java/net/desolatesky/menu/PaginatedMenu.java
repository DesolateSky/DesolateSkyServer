package net.desolatesky.menu;

import net.desolatesky.menu.action.ClickAction;
import net.desolatesky.menu.item.MenuButton;
import net.desolatesky.menu.pattern.Pattern;
import net.desolatesky.player.DSPlayer;
import net.kyori.adventure.text.Component;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.inventory.click.Click;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class PaginatedMenu extends GUIMenu {

    public static MenuButton nextPageButton(ItemStack itemStack) {
        return MenuButton.simple(itemStack, click -> {
            if (!(click.menu() instanceof  final PaginatedMenu menu)) {
                return ClickAction.Result.CANCEL;
            }
            menu.nextPage();
            return ClickAction.Result.CANCEL;
        });
    }

    public static MenuButton previousPageButton(ItemStack itemStack) {
        return MenuButton.simple(itemStack, click -> {
            if (!(click.menu() instanceof final PaginatedMenu menu)) {
                return ClickAction.Result.CANCEL;
            }
            menu.previousPage();
            return ClickAction.Result.CANCEL;
        });
    }

    protected final List<? extends MenuButton> pageItems;
    protected final NavigableMap<Integer, Integer> indexToSlotMap;
    protected final NavigableMap<Integer, Integer> slotToIndexMap = new TreeMap<>();
    protected int currentPage;

    public PaginatedMenu(
            DSPlayer player,
            InventoryType type,
            Component title,
            Map<Integer, MenuButton> menuItems,
            Map<Integer, ClickAction> clickActions,
            List<? extends MenuButton> pageItems,
            List<Integer> pageSlots,
            List<Pattern> patterns,
            @Nullable ClickAction defaultClickAction
    ) {
        super(player, type, title, menuItems, clickActions, patterns, defaultClickAction);
        this.pageItems = pageItems;
        this.indexToSlotMap = new TreeMap<>();
        for (int i = 0; i < pageSlots.size(); i++) {
            final int slot = pageSlots.get(i);
            this.indexToSlotMap.put(i, slot);
        }
        for (final Map.Entry<Integer, Integer> entry : this.indexToSlotMap.entrySet()) {
            this.slotToIndexMap.put(entry.getValue(), entry.getKey());
        }
        this.currentPage = 0;
    }

    @Override
    public Component title() {
        return super.title();
    }

    @Override
    public void setTitle(Component title) {
        super.setTitle(title);
    }

    @Override
    public InventoryType inventoryType() {
        return super.inventoryType();
    }

    @Override
    public ClickResult click(AbstractInventory clickedInventory, Click click, int slot) {
        return super.click(clickedInventory, click, slot);
    }

    @Override
    public void setItem(int slot, MenuButton item, boolean replace) {
        super.setItem(slot, item, replace);
    }

    @Override
    public void open() {
        super.open();
    }

    @Override
    public void refresh() {
        super.refresh();
        this.fillPage();
    }

    @Override
    public void refresh(int slot) {
        super.refresh(slot);
    }

    @Override
    public MenuButton getItem(int slot) {
        final int pageIndex = this.getIndexFromSlot(slot);
        if (pageIndex == -1 || pageIndex >= this.pageItems.size()) {
            return super.getItem(slot);
        }
        return this.pageItems.get(pageIndex);
    }

    public void nextPage() {
        if (this.currentPage < this.calculateTotalPages() - 1) {
            this.currentPage++;
            this.refresh();
        }
    }

    public void previousPage() {
        if (this.currentPage > 0) {
            this.currentPage--;
            this.refresh();
        }
    }

    private void fillPage() {
        final int startIndex = this.currentPage * this.getPageItemsSize();
        final int endIndex = Math.min(startIndex + this.getPageItemsSize(), this.pageItems.size());

        for (int i = startIndex; i < endIndex; i++) {
            final int index = i - startIndex;
            final int slot = this.indexToSlotMap.getOrDefault(index, -1);
            if (slot == -1 || slot >= this.inventoryType().getSize()) {
                throw new IllegalArgumentException("Slot " + slot + " is out of bounds for inventory type " + this.inventoryType());
            }
            final MenuButton button = this.pageItems.get(i);
            this.setItem(slot, button, true);
        }
    }

    public int calculateTotalPages() {
        if (this.pageItems.isEmpty() || this.slotToIndexMap.isEmpty()) {
            return 0;
        }
        final int itemsPerPage = this.slotToIndexMap.size();
        return (int) Math.ceil((double) this.pageItems.size() / itemsPerPage);
    }

    public int getIndexFromSlot(int slot) {
        return this.slotToIndexMap.getOrDefault(slot, -1);
    }

    private int getPageItemsSize() {
        return this.pageItems.size();
    }

}
