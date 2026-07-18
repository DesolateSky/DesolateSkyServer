package net.desolatesky.entity;

import net.desolatesky.island.Island;
import net.minestom.server.entity.Entity;

import java.util.UUID;

public interface IslandEntity {

    UUID id();

    int entityId();

    Entity asEntity();

    Island island();

}
