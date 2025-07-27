package net.desolatesky.menu.pattern;

import com.google.common.base.Preconditions;
import net.desolatesky.menu.Menu;
import net.desolatesky.menu.item.MenuButton;
import net.desolatesky.menu.item.SimpleMenuButton;
import net.minestom.server.inventory.InventoryType;

import java.util.List;

public final class BorderPattern implements Pattern {

    private final List<? extends MenuButton> items;

    public BorderPattern(List<? extends MenuButton> items) {
        Preconditions.checkArgument(!items.isEmpty(), "Items list cannot be empty");
        this.items = items;
    }

    public BorderPattern(MenuButton menuButton) {
        this.items = List.of(menuButton);
    }

    @Override
    public void apply(Menu menu) {
        final int size = this.getSize(menu);
        final int rows = size / 9;
        int currentIndex = 0;
        for (int i = 0; i < 9; i++) {
            final MenuButton item = this.items.get(currentIndex);
            menu.setItem(i, item, false);
            currentIndex = this.getNextIndex(currentIndex);
        }
        for (int i = 1; i < rows - 1; i++) {
            final int leftIndex = i * 9;
            final int rightIndex = leftIndex + 8;
            menu.setItem(leftIndex, this.items.get(currentIndex), false);
            currentIndex = this.getNextIndex(currentIndex);
            menu.setItem(rightIndex, this.items.get(currentIndex), false);
            currentIndex = this.getNextIndex(currentIndex);
        }
        for (int i = size - 9; i < size; i++) {
            final MenuButton item = this.items.get(currentIndex);
            menu.setItem(i, item, false);
            currentIndex = this.getNextIndex(currentIndex);
        }
    }

    private int getNextIndex(int index) {
        return (index + 1) % this.items.size();
    }

    private int getSize(Menu menu) {
        final InventoryType inventoryType = menu.inventoryType();
        return switch (inventoryType) {
            case InventoryType.CHEST_1_ROW,
                 InventoryType.CHEST_2_ROW,
                 InventoryType.CHEST_3_ROW,
                 InventoryType.CHEST_4_ROW,
                 InventoryType.CHEST_5_ROW,
                 InventoryType.CHEST_6_ROW -> inventoryType.getSize();
            default -> throw new IllegalArgumentException("Unsupported inventory type: " + inventoryType);
        };
    }
}
