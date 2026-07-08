package com.fisherl.desolatesky.loot;

import com.fisherl.desolatesky.util.Pair;
import com.fisherl.desolatesky.util.collection.WeightedCollection;
import net.kyori.adventure.key.Key;

import java.util.random.RandomGenerator;

public final class LootTable {

    private final WeightedCollection<ItemLoot> loot;

    public LootTable(WeightedCollection<ItemLoot> loot) {
        this.loot = loot;
    }

    public Pair<Key, Integer> roll(RandomGenerator randomGenerator) {
        final ItemLoot loot = this.loot.next(randomGenerator);
        if (loot.min() == loot.max()) {
            return new Pair<>(loot.itemId(), loot.min());
        }
        final int amount = randomGenerator.nextInt(loot.min(), loot.max());
        return new Pair<>(loot.itemId(), amount);
    }
}
