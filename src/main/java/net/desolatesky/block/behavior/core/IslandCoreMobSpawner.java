package net.desolatesky.block.behavior.core;

import net.desolatesky.entity.EntityIds;
import net.desolatesky.item.ItemIds;
import net.desolatesky.util.collection.WeightedCollection;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.random.RandomGenerator;

public final class IslandCoreMobSpawner {

    // TODO - move to actual non-static storage
    public static final @Unmodifiable Map<Key, IslandCoreMobSpawner> SPAWNERS = Map.of(
            IslandCoreMobSpawnerIds.SILVERFISH, new IslandCoreMobSpawner(IslandCoreMobSpawnerIds.SILVERFISH, ItemIds.VOID_INFUSED_POTATO, new WeightedCollection<Key>().add(1.0,EntityIds.VOID_SILVERFISH)),
            IslandCoreMobSpawnerIds.RABBIT, new IslandCoreMobSpawner(IslandCoreMobSpawnerIds.RABBIT, ItemIds.VOID_INFUSED_BUSH, new WeightedCollection<Key>().add(1.0,EntityIds.VOID_RABBIT)),
            IslandCoreMobSpawnerIds.PIG, new IslandCoreMobSpawner(IslandCoreMobSpawnerIds.PIG, ItemIds.VOID_INFUSED_CARROT, new WeightedCollection<Key>().add(1.0,EntityIds.VOID_PIG))
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
