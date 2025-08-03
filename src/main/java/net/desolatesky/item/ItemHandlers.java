package net.desolatesky.item;

import net.desolatesky.item.category.ItemCategories;
import net.desolatesky.item.handler.BasicItemHandler;
import net.desolatesky.item.handler.ItemHandler;
import net.desolatesky.item.handler.breaking.MiningLevels;
import net.desolatesky.item.handler.breaking.calculator.BreakTimeCalculator;
import net.desolatesky.item.handler.breaking.calculator.BreakTimeCalculators;
import net.desolatesky.item.handler.breaking.calculator.CategoryBreakTimeCalculator;
import net.desolatesky.item.tool.ToolItemHandler;
import net.desolatesky.tag.TagBuilder;

import java.util.Collections;
import java.util.Set;

public final class ItemHandlers {

    private ItemHandlers() {
        throw new UnsupportedOperationException();
    }

    public static final ItemHandler PETRIFIED_STICK = new BasicItemHandler(ItemKeys.PETRIFIED_STICK, BreakTimeCalculator.BLOCK_TIME, Set.of(ItemCategories.STICK), MiningLevels.NONE);
    public static final ItemHandler PETRIFIED_PLANKS = new BasicItemHandler(ItemKeys.PETRIFIED_PLANKS, BreakTimeCalculator.BLOCK_TIME, Set.of(ItemCategories.PLANKS), MiningLevels.NONE);
    public static final ItemHandler PETRIFIED_SLAB = new BasicItemHandler(ItemKeys.PETRIFIED_SLAB, BreakTimeCalculator.BLOCK_TIME, Set.of(ItemCategories.WOODEN_SLABS), MiningLevels.NONE);
    public static final ItemHandler DEAD_LEAVES = new BasicItemHandler(ItemKeys.DEAD_LEAVES, BreakTimeCalculator.BLOCK_TIME, Set.of(ItemCategories.COMPOSTABLE), TagBuilder.create().with(ItemTags.COMPOSTER_VALUE, 0.5).build(), MiningLevels.NONE);
    public static final ItemHandler DUST_BLOCK = new BasicItemHandler(ItemKeys.DUST_BLOCK, BreakTimeCalculator.BLOCK_TIME, Collections.emptySet(), MiningLevels.NONE);

    // TOOLS
    public static final ItemHandler AXE_HANDLER = new ToolItemHandler(ItemKeys.AXE, BreakTimeCalculators.AXE, Set.of(ItemCategories.AXE));
}
