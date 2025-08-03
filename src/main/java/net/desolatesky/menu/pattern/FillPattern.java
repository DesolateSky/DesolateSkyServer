package net.desolatesky.menu.pattern;

import net.desolatesky.menu.Menu;
import net.desolatesky.menu.item.MenuButton;

import java.util.Collection;

public final class FillPattern implements Pattern {

    private final MenuButton fillItem;
    private final Collection<Integer> excludeSlots;

    public FillPattern(MenuButton fillItem, Collection<Integer> excludeSlots) {
        this.fillItem = fillItem;
        this.excludeSlots = excludeSlots;
    }

    @Override
    public void apply(Menu menu) {
        for (int i = 0; i < menu.inventoryType().getSize(); i++) {
            if (!this.excludeSlots.contains(i)) {
                menu.setItem(i, this.fillItem, false);
            }
        }
    }

}
