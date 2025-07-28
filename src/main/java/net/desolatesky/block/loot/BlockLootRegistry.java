package net.desolatesky.block.loot;

import net.desolatesky.block.BlockKeys;
import net.desolatesky.block.handler.custom.ComposterBlockHandler;
import net.desolatesky.block.handler.custom.SifterBlockHandler;
import net.desolatesky.item.DSItems;
import net.desolatesky.loot.generator.ItemStackLootGenerator;
import net.desolatesky.loot.table.LootTable;
import net.desolatesky.loot.table.LootTableRegistry;
import net.desolatesky.loot.type.ItemStackLoot;
import net.kyori.adventure.key.Key;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BlockLootRegistry extends LootTableRegistry {

    public static BlockLootRegistry create() {
        return new BlockLootRegistry(new HashMap<>());
    }

    private BlockLootRegistry(Map<Key, LootTable> blockLoots) {
        super(blockLoots);
    }

    public void load() {
        this.register(BlockKeys.COMPOSTER, LootTable.create(Map.of(
                ComposterBlockHandler.LOOT_GENERATOR_TYPE,
                ItemStackLootGenerator.create(ComposterBlockHandler.LOOT_GENERATOR_TYPE, List.of(new ItemStackLoot(DSItems.DIRT, 1, 1, 1)), 1, 1)
        )));
        this.register(BlockKeys.DUST_BLOCK, LootTable.create(Map.of(
                SifterBlockHandler.LOOT_GENERATOR_TYPE,
                ItemStackLootGenerator.create(SifterBlockHandler.LOOT_GENERATOR_TYPE, List.of(new ItemStackLoot(DSItems.COPPER_DUST, 1, 0, 2)), 1, 3)
        )));
    }

}
