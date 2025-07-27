package net.desolatesky.item;

import net.desolatesky.item.category.ItemCategories;
import net.desolatesky.item.handler.ItemHandler;
import net.desolatesky.item.handler.breaking.MiningLevels;
import net.desolatesky.item.handler.breaking.calculator.BreakTimeCalculators;

import java.util.Set;

public final class ItemHandlers {

    private ItemHandlers() {
        throw new UnsupportedOperationException();
    }

    public static final ItemHandler WOODEN_AXE = new ItemHandler(ItemKeys.WOODEN_AXE, BreakTimeCalculators.AXE, Set.of(ItemCategories.AXE), MiningLevels.WOOD);
}
