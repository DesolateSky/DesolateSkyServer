package net.desolatesky.entity;

import net.desolatesky.island.Island;
import net.kyori.adventure.key.Key;
import net.minestom.server.entity.Entity;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public interface EntityManager {

    @Nullable IslandEntity createEntity(Key entityId, Island island, Consumer<Entity> tagApplier);

    void initialize();

}
