package net.desolatesky.loot.generator;

import net.desolatesky.block.BlockKeys;
import net.desolatesky.block.handler.custom.SifterBlockHandler;
import net.desolatesky.item.DSItems;
import net.desolatesky.loot.registry.LootGeneratorRegistry;
import net.desolatesky.loot.type.ItemStackLoot;

import java.util.List;

public final class LootGenerators {

    private LootGenerators() {
        throw new UnsupportedOperationException();
    }

    public static void register(LootGeneratorRegistry registry) {
        registry.register(ItemStackLootGenerator.create(SifterBlockHandler.LOOT_GENERATOR_TYPE, BlockKeys.DUST_BLOCK, List.of(new ItemStackLoot(DSItems.DUST, 1, 4, 6)), 1, 1));
    }

}
