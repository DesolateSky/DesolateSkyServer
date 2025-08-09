package net.desolatesky.entity;

import net.desolatesky.instance.DSInstance;
import net.desolatesky.instance.InstancePoint;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.PlayerHand;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SimpleEntity extends Entity implements DSEntity {

    protected final EntityKey entityKey;

    public SimpleEntity(@NotNull EntityType entityType, @NotNull UUID uuid, EntityKey entityKey) {
        super(entityType, uuid);
        this.entityKey = entityKey;
    }

    public SimpleEntity(@NotNull EntityType entityType, EntityKey entityKey) {
        super(entityType);
        this.entityKey = entityKey;
    }

    @Override
    public DSInstance getInstance() {
        return (DSInstance) this.instance;
    }

    @Override
    public void onClick(DSEntity clicker, Point interactionPoint, PlayerHand hand) {

    }

    @Override
    public void onPunch(DSEntity attacker) {

    }

    @Override
    public InstancePoint<? extends Point> getInstancePosition() {
        return new InstancePoint<>(this.getInstance(), this.position);
    }

    @Override
    public EntityKey key() {
        return this.entityKey;
    }


}
