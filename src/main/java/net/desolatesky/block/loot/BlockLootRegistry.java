package net.desolatesky.block.loot;

import net.desolatesky.block.BlockKeys;
import net.desolatesky.block.handler.custom.SifterBlockHandler;
import net.desolatesky.item.DSItems;
import net.desolatesky.loot.generator.ItemStackLootGenerator;
import net.desolatesky.loot.table.LootTable;
import net.desolatesky.loot.table.LootTableRegistry;
import net.desolatesky.loot.type.ItemStackLoot;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BlockLootRegistry extends LootTableRegistry {

    public static BlockLootRegistry create() {
        final BlockLootRegistry blockLootRegistry = new BlockLootRegistry(new HashMap<>());
        blockLootRegistry.load();
        return blockLootRegistry;
    }

    private BlockLootRegistry(Map<Key, LootTable> blockLoots) {
        super(blockLoots);
    }

    private void load() {
        this.register(BlockKeys.DUST_BLOCK, LootTable.create(Map.of(
                SifterBlockHandler.LOOT_GENERATOR_TYPE,
                ItemStackLootGenerator.create(SifterBlockHandler.LOOT_GENERATOR_TYPE, BlockKeys.DUST_BLOCK, List.of(new ItemStackLoot(DSItems.COPPER_DUST, 1, 0, 2)), 1, 3)
        )));
    }

}
