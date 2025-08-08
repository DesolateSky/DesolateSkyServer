package net.desolatesky.block.entity.custom.crop;

import net.desolatesky.loot.LootContext;
import net.desolatesky.loot.context.BlockBreakContext;
import net.desolatesky.loot.generator.ItemStackLootGenerator;
import net.desolatesky.loot.generator.LootGenerator;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.item.ItemStack;

import java.util.Collection;
import java.util.List;

public final class CropLootGenerator implements LootGenerator {

    private final ItemStackLootGenerator notFullyGrownLootGenerator;
     private final ItemStackLootGenerator fullyGrownLootGenerator;

    public CropLootGenerator(ItemStackLootGenerator notFullyGrownLootGenerator, ItemStackLootGenerator fullyGrownLootGenerator) {
        this.notFullyGrownLootGenerator = notFullyGrownLootGenerator;
        this.fullyGrownLootGenerator = fullyGrownLootGenerator;
    }

    @Override
    public Collection<ItemStack> generateLoot(LootContext context) {
        if (!(context instanceof final BlockBreakContext blockBreakContext)) {
            return List.of();
        }
        final Block block = blockBreakContext.block();
        final BlockHandler blockEntity = block.handler();
        if (!(blockEntity instanceof final CropBlockEntity<?> cropBlockEntity)) {
            return List.of();
        }
        if (cropBlockEntity.isFullyGrown()) {
            return this.fullyGrownLootGenerator.generateLoot(context);
        } else {
            return this.notFullyGrownLootGenerator.generateLoot(context);
        }
    }
    
}
