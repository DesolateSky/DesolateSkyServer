package net.desolatesky.loot;

import net.desolatesky.item.ItemFactory;
import net.desolatesky.util.Pair;
import net.desolatesky.util.collection.WeightedCollection;
import net.kyori.adventure.key.Key;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.random.RandomGenerator;

public final class LootTable {

    private final List<WeightedCollection<ItemLoot>> lootPools;

    public LootTable(List<WeightedCollection<ItemLoot>> lootPools) {
        this.lootPools = lootPools;
    }

    public LootTable(WeightedCollection<ItemLoot> loot) {
        this(List.of(loot));
    }

    public List<Pair<Key, Integer>> roll(RandomGenerator randomGenerator) {
        final List<Pair<Key, Integer>> list = new ArrayList<>();
        for (final WeightedCollection<ItemLoot> collection : this.lootPools) {
            final ItemLoot loot = collection.next(randomGenerator);
            if (loot.itemId().equals(Material.AIR.key())) {
                continue;
            }
            if (loot.min() == loot.max()) {
                list.add(new Pair<>(loot.itemId(), loot.min()));
                continue;
            }
            final int amount = randomGenerator.nextInt(loot.min(), loot.max());
            list.add(new Pair<>(loot.itemId(), amount));
        }
        return list;
    }

    public List<ItemStack> roll(RandomGenerator randomGenerator, ItemFactory itemFactory) {
        return this.roll(randomGenerator)
                .stream()
                .map(pair -> {
                    final ItemStack itemStack = itemFactory.getDefaultItem(pair.first());
                    if (itemStack == null) {
                        return null;
                    }
                    return itemStack.withAmount(pair.second());
                })
                .filter(Objects::nonNull)
                .toList();
    }
}
