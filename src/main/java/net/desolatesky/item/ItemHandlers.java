package net.desolatesky.item;

import net.desolatesky.item.category.ItemCategories;
import net.desolatesky.item.handler.ItemHandler;
import net.desolatesky.item.handler.breaking.MiningLevels;
import net.desolatesky.item.handler.breaking.calculator.BreakTimeCalculator;
import net.desolatesky.item.handler.breaking.calculator.BreakTimeCalculators;
import net.desolatesky.tag.TagBuilder;

import java.util.Collections;
import java.util.Set;

public final class ItemHandlers {

    private ItemHandlers() {
        throw new UnsupportedOperationException();
    }

    public static final ItemHandler WOODEN_AXE = new ItemHandler(ItemKeys.WOODEN_AXE, BreakTimeCalculators.AXE, Set.of(ItemCategories.AXE), MiningLevels.WOOD);
    public static final ItemHandler PETRIFIED_STICK = new ItemHandler(ItemKeys.PETRIFIED_STICK, BreakTimeCalculator.BLOCK_TIME, Set.of(ItemCategories.STICK), MiningLevels.NONE);
    public static final ItemHandler PETRIFIED_PLANKS = new ItemHandler(ItemKeys.PETRIFIED_PLANKS, BreakTimeCalculator.BLOCK_TIME, Set.of(ItemCategories.PLANKS), MiningLevels.NONE);
    public static final ItemHandler PETRIFIED_SLAB = new ItemHandler(ItemKeys.PETRIFIED_SLAB, BreakTimeCalculator.BLOCK_TIME, Set.of(ItemCategories.WOODEN_SLABS), MiningLevels.NONE);
    public static final ItemHandler DEAD_LEAVES = new ItemHandler(ItemKeys.DEAD_LEAVES, BreakTimeCalculator.BLOCK_TIME, Set.of(ItemCategories.COMPOSTABLE), MiningLevels.NONE, TagBuilder.create().with(ItemTags.COMPOSTER_VALUE, 0.5).build());
    public static final ItemHandler DUST_BLOCK = new ItemHandler(ItemKeys.DUST_BLOCK, BreakTimeCalculator.BLOCK_TIME, Collections.emptySet(), MiningLevels.NONE);

}
