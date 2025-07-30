package net.desolatesky.item.handler.breaking.calculator;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.entity.BlockEntity;
import net.desolatesky.block.handler.DSBlockHandler;
import net.desolatesky.item.handler.ItemHandler;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;

import java.time.Duration;

public interface BreakTimeCalculator {

    Duration calculateBreakTime(DesolateSkyServer server, ItemHandler itemHandler, ItemStack usedItem, Block block);

    BreakTimeCalculator BLOCK_TIME = (server, _, _, block) -> {
        final DSBlockHandler blockHandler = server.blockRegistry().getHandlerForBlock(block);
        if (blockHandler == null) {
            return BlockEntity.UNBREAKABLE_BREAK_TIME;
        }
        return blockHandler.breakTime();
    };

}
