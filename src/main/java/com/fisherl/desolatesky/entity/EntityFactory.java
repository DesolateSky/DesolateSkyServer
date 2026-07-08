package com.fisherl.desolatesky.entity;

import com.fisherl.desolatesky.island.Island;
import net.kyori.adventure.key.Key;
import net.minestom.server.entity.Entity;

import java.util.Optional;
import java.util.function.Consumer;

public interface EntityFactory {

    Optional<IslandEntity> createEntity(Key entityId, Island island, Consumer<Entity> tagApplier);

    void initialize();

}
