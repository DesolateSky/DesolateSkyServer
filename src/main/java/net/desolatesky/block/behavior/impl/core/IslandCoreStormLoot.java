package net.desolatesky.block.behavior.impl.core;

import net.desolatesky.item.ItemIds;
import net.desolatesky.loot.ItemLoot;
import net.desolatesky.loot.LootTable;
import net.desolatesky.util.Namespace;
import net.desolatesky.util.collection.WeightedCollection;
import net.kyori.adventure.key.Key;

public record IslandCoreStormLoot(Key key, LootTable lootTable) {

    public static final IslandCoreStormLoot DUST_STORM = new IslandCoreStormLoot(Namespace.key("dust_storm"),
            new LootTable(new WeightedCollection<ItemLoot>().add(1, new ItemLoot(ItemIds.DUST, 1, 3)))
    );

}
