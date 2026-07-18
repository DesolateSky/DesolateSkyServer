package net.desolatesky.block.behavior.core;

import net.desolatesky.entity.EntityIds;
import net.desolatesky.item.ItemIds;
import net.desolatesky.util.collection.WeightedCollection;
import net.kyori.adventure.key.Key;

import java.util.Map;
import java.util.random.RandomGenerator;

public final class IslandCoreMobSpawner {

    // TODO - move to actual factory class
    public static final Map<Key, IslandCoreMobSpawner> SPAWNERS = Map.of(
            IslandCoreMobSpawnerIds.SILVERFISH, new IslandCoreMobSpawner(IslandCoreMobSpawnerIds.SILVERFISH, ItemIds.ENTITY_ATTRACTOR_SILVERFISH, new WeightedCollection<Key>().add(1.0,EntityIds.VOID_SILVERFISH)),
            IslandCoreMobSpawnerIds.RABBIT, new IslandCoreMobSpawner(IslandCoreMobSpawnerIds.RABBIT, ItemIds.VOID_INFUSED_BUSH, new WeightedCollection<Key>().add(1.0,EntityIds.VOID_RABBIT))
    );

    private final Key key;
    private final Key itemDisplayKey;
    private final WeightedCollection<Key> entities;

    public IslandCoreMobSpawner(Key key, Key itemDisplayKey, WeightedCollection<Key> entities) {
        this.key = key;
        this.itemDisplayKey = itemDisplayKey;
        this.entities = entities;
    }

    public Key rollEntity(RandomGenerator randomGenerator) {
        return this.entities.next(randomGenerator);
    }

    public Key key() {
        return this.key;
    }

    public Key itemDisplayKey() {
        return this.itemDisplayKey;
    }
}
