package net.desolatesky.loot.table;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Map;

public class LootTableRegistry {

    private final Map<Key, LootTable> blockLoots;

    public LootTableRegistry(Map<Key, LootTable> blockLoots) {
        this.blockLoots = blockLoots;
    }

    public @UnknownNullability LootTable getLootTable(Key key) {
        return this.blockLoots.get(key);
    }

    public @UnknownNullability LootTable getLootTable(Key key, LootTable defaultLootTable) {
        return this.blockLoots.getOrDefault(key, defaultLootTable);
    }

    protected void register(Key key, LootTable lootTable) {
        this.blockLoots.put(key, lootTable);
    }

}
