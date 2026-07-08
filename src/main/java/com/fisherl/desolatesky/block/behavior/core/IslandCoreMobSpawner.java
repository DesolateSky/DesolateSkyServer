package com.fisherl.desolatesky.block.behavior.core;

import com.fisherl.desolatesky.entity.EntityIds;
import com.fisherl.desolatesky.item.ItemIds;
import com.fisherl.desolatesky.util.collection.WeightedCollection;
import net.kyori.adventure.key.Key;

import java.util.Map;
import java.util.random.RandomGenerator;

public final class IslandCoreMobSpawner {

    // TODO - move to actual factory class
    public static final Map<Key, IslandCoreMobSpawner> SPAWNERS = Map.of(
            IslandCoreMobSpawnerIds.SILVERFISH, new IslandCoreMobSpawner(IslandCoreMobSpawnerIds.SILVERFISH, ItemIds.ENTITY_ATTRACTOR_SILVERFISH, new WeightedCollection<Key>().add(1.0,EntityIds.SILVERFISH))
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
