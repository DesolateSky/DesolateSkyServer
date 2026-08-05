package net.desolatesky.loot;

import net.desolatesky.block.behavior.impl.core.IslandCoreStormLoot;
import net.kyori.adventure.key.Key;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class LootFactory {

    private final Map<Key, LootTable> lootMap = new HashMap<>();

    public LootFactory() {
    }

    public void initialize() {
        this.lootMap.put(IslandCoreStormLoot.DUST_STORM.key(), IslandCoreStormLoot.DUST_STORM.lootTable());
    }

    public Optional<LootTable> getLootTable(Key key) {
        return Optional.ofNullable(this.lootMap.get(key));
    }
}
