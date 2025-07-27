package net.desolatesky.item.handler.breaking.calculator;

import net.desolatesky.block.handler.DSBlockHandler;
import net.desolatesky.item.category.ItemCategory;
import net.desolatesky.item.handler.ItemHandler;
import net.desolatesky.item.handler.breaking.MiningLevel;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public final class CategoryBreakTimeCalculator implements BreakTimeCalculator {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryBreakTimeCalculator.class);

    private final double categorySpeedMultiplier;

    public CategoryBreakTimeCalculator(double categorySpeedMultiplier) {
        this.categorySpeedMultiplier = categorySpeedMultiplier;
    }

    @Override
    public Duration calculateBreakTime(ItemHandler itemHandler, ItemStack usedItem, Block block) {
        final DSBlockHandler blockHandler = (DSBlockHandler) block.handler();
        if (blockHandler == null || blockHandler.isUnbreakable()) {
            return DSBlockHandler.UNBREAKABLE_BREAK_TIME;
        }
        double breakTime = blockHandler.settings().breakTime();
        final MiningLevel miningLevel = itemHandler.miningLevel();
        if (!miningLevel.isAtLeast(blockHandler.miningLevel())) {
            return DSBlockHandler.UNBREAKABLE_BREAK_TIME;
        }
        breakTime *= miningLevel.speedMultiplier();
        boolean isCategoryItem = false;
        for (final ItemCategory category : itemHandler.categories()) {
            if (category.appliesTo(blockHandler)) {
                isCategoryItem = true;
                break;
            }
        }
        if (isCategoryItem) {
            breakTime *= this.categorySpeedMultiplier;
        }
        return Duration.ofMillis((int) breakTime);
    }

}
