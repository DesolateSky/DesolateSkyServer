package com.fisherl.desolatesky.block.behavior.core;

import com.fisherl.desolatesky.item.ItemIds;
import com.fisherl.desolatesky.loot.ItemLoot;
import com.fisherl.desolatesky.loot.LootTable;
import com.fisherl.desolatesky.util.Namespace;
import com.fisherl.desolatesky.util.collection.WeightedCollection;
import net.kyori.adventure.key.Key;

public record IslandCoreStormLoot(Key key, LootTable lootTable) {

    public static final IslandCoreStormLoot DUST_STORM = new IslandCoreStormLoot(Namespace.key("dust_storm"),
            new LootTable(new WeightedCollection<ItemLoot>().add(1, new ItemLoot(ItemIds.DUST, 1, 3)))
    );

}
