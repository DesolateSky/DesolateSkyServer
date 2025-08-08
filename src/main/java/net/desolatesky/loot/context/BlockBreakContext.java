package net.desolatesky.loot.context;

import net.desolatesky.block.handler.DSBlockHandler;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.loot.LootContext;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.random.RandomGenerator;

public record BlockBreakContext(RandomGenerator randomSource, DSInstance instance, ItemStack itemUsed, DSBlockHandler blockHandler, Block block, Point blockLocation, @Nullable LootContext individualLootContext) implements LootContext {

    public static BlockBreakContext createBlockBreakContext(RandomGenerator randomSource, DSInstance instance, ItemStack itemUsed, DSBlockHandler blockHandler, Block block, Point blockLocation) {
        return new BlockBreakContext(randomSource, instance, itemUsed, blockHandler, block, blockLocation, null);
    }

    public static BlockBreakContext createBlockBreakContext(DSInstance instance, ItemStack itemUsed, DSBlockHandler blockHandler, Block block, Point blockLocation) {
        return createBlockBreakContext(instance.randomSource(), instance, itemUsed, blockHandler, block, blockLocation);
    }

    @Override
    public LootContext perElementLootContext() {
        return this.individualLootContext != null ? this.individualLootContext : this;
    }

}
