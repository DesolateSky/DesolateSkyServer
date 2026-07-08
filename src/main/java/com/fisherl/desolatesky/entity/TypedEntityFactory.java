package com.fisherl.desolatesky.entity;

import com.fisherl.desolatesky.entity.type.CraftingCatalystEntity;
import com.fisherl.desolatesky.entity.type.IslandCoreSpawnerEntity;
import com.fisherl.desolatesky.entity.type.Silverfish;
import com.fisherl.desolatesky.island.Island;
import net.kyori.adventure.key.Key;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.ItemEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public final class TypedEntityFactory implements EntityFactory {

    private final Map<Key, BiFunction<Island, Consumer<Entity>, IslandEntity>> entityCreators = new HashMap<>();

    @Override
    public Optional<IslandEntity> createEntity(Key entityType, Island island, Consumer<Entity> tagApplier) {
        return Optional.ofNullable(this.entityCreators.get(entityType))
                .map(func -> func.apply(island, tagApplier));
    }

    @Override
    public void initialize() {
        this.entityCreators.put(EntityIds.SILVERFISH, Silverfish::new);
        this.entityCreators.put(EntityIds.ISLAND_CORE_SPAWNER_DISPLAY, IslandCoreSpawnerEntity::new);
        this.entityCreators.put(EntityIds.CRAFTING_CATALYST, CraftingCatalystEntity::new);
        this.entityCreators.put(EntityIds.ITEM, DSItemEntity::new);
    }
}
