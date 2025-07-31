package net.desolatesky.item.handler.breaking.calculator;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.entity.BlockEntity;
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
    public Duration calculateBreakTime(DesolateSkyServer server, ItemHandler itemHandler, ItemStack usedItem, Block block) {
        final DSBlockHandler blockHandler = server.blockRegistry().getHandlerForBlock(block);
        if (blockHandler == null || blockHandler.isUnbreakable()) {
            return BlockEntity.UNBREAKABLE_BREAK_TIME;
        }
        double breakTime = blockHandler.settings().breakTime().toMillis();
        final MiningLevel miningLevel = itemHandler.getMiningLevelFor(block);
        if (!miningLevel.isAtLeast(blockHandler.miningLevel())) {
            return BlockEntity.UNBREAKABLE_BREAK_TIME;
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
