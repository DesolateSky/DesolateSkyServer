package net.desolatesky.entity;

import net.desolatesky.entity.type.CraftingCatalystEntity;
import net.desolatesky.entity.type.IslandCoreSpawnerEntity;
import net.desolatesky.entity.type.VoidSilverfish;
import net.desolatesky.entity.type.VoidRabbit;
import net.desolatesky.island.Island;
import net.kyori.adventure.key.Key;
import net.minestom.server.entity.Entity;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public final class TypedEntityFactory implements EntityFactory {

    private final Map<Key, BiFunction<Island, Consumer<Entity>, IslandEntity>> entityCreators = new HashMap<>();

    @Override
    public @Nullable IslandEntity createEntity(Key entityType, Island island, Consumer<Entity> tagApplier) {
        final BiFunction<Island, Consumer<Entity>, IslandEntity> creator = this.entityCreators.get(entityType);
        if (creator == null) {
            return null;
        }
        return creator.apply(island, tagApplier);
    }

    @Override
    public void initialize() {
        this.entityCreators.put(EntityIds.VOID_SILVERFISH, VoidSilverfish::new);
        this.entityCreators.put(EntityIds.VOID_RABBIT, VoidRabbit::new);
        this.entityCreators.put(EntityIds.ISLAND_CORE_SPAWNER_DISPLAY, IslandCoreSpawnerEntity::new);
        this.entityCreators.put(EntityIds.CRAFTING_CATALYST, CraftingCatalystEntity::new);
        this.entityCreators.put(EntityIds.ITEM, DSItemEntity::new);
    }
}
