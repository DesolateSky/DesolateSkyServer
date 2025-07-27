package net.desolatesky.item.handler.breaking.calculator;

import net.desolatesky.block.handler.DSBlockHandler;
import net.desolatesky.item.handler.ItemHandler;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;

import java.time.Duration;

public interface BreakTimeCalculator {

    Duration calculateBreakTime(ItemHandler itemHandler, ItemStack usedItem, Block block);

    BreakTimeCalculator BLOCK_TIME = (_, _, block) -> {
        final DSBlockHandler blockHandler = (DSBlockHandler) block.handler();
        if (blockHandler == null) {
            return DSBlockHandler.UNBREAKABLE_BREAK_TIME;
        }
        final int breakTime = blockHandler.breakTime();
        return Duration.ofMillis(breakTime);
    };

}
