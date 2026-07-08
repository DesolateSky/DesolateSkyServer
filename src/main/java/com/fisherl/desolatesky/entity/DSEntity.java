package com.fisherl.desolatesky.entity;

import com.fisherl.desolatesky.island.Island;
import com.fisherl.desolatesky.world.DSWorld;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;

import java.util.UUID;
import java.util.function.Consumer;

public abstract class DSEntity<T extends DSEntity<T>> extends Entity implements IslandEntity {

    protected final Island island;

    public DSEntity(
            EntityType entityType,
            UUID uuid,
            Island island,
            Consumer<Entity> tagApplier
    ) {
        super(entityType, uuid);
        this.island = island;
        tagApplier.accept(this);
        this.initialize();
    }

    public DSEntity(
            EntityType entityType,
            Island island,
            Consumer<Entity> tagApplier
    ) {
        super(entityType);
        this.island = island;
        tagApplier.accept(this);
        this.initialize();
    }

    protected abstract void initialize();

    @Override
    public final void tick(long time) {
        super.tick(time);
        this.onTick(time);
    }

    protected abstract void dropItems();

    protected abstract void onTick(long time);

    public Island island() {
        return this.island;
    }

    public DSWorld world() {
        return (DSWorld) this.instance;
    }

    @Override
    public UUID id() {
        return this.getUuid();
    }

    @Override
    public int entityId() {
        return this.getEntityId();
    }

    @Override
    public Entity asEntity() {
        return this;
    }
}
