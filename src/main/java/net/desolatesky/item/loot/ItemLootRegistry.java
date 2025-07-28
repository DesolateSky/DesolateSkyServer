package net.desolatesky.item.loot;

import net.desolatesky.loot.table.LootTable;
import net.desolatesky.loot.table.LootTableRegistry;
import net.kyori.adventure.key.Key;

import java.util.HashMap;
import java.util.Map;

public class ItemLootRegistry extends LootTableRegistry {

    public static ItemLootRegistry create() {
        return new ItemLootRegistry(new HashMap<>());
    }

    private ItemLootRegistry(Map<Key, LootTable> itemLoots) {
        super(itemLoots);
    }

    public void load() {

    }
        
}
