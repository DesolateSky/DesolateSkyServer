package net.desolatesky.entity;

import net.desolatesky.island.Island;
import net.desolatesky.world.DSWorld;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.UUID;
import java.util.function.Consumer;

public class DSItemEntity<T extends DSItemEntity<T>> extends ItemEntity implements IslandEntity {

    protected final Island island;

    public DSItemEntity(
            Island island,
            Consumer<Entity> tagApplier
    ) {
        super(ItemStack.AIR);
        this.island = island;
        tagApplier.accept(this);
        this.initialize();
    }

    public DSItemEntity(
            ItemStack itemStack,
            Island island,
            Consumer<Entity> tagApplier
    ) {
        super(itemStack);
        this.island = island;
        tagApplier.accept(this);
        this.initialize();
    }

    @ApiStatus.OverrideOnly
    protected void initialize() {

    }

    @Override
    public final void tick(long time) {
        super.tick(time);
        this.onTick(time);
    }

    @ApiStatus.OverrideOnly
    protected void onTick(long time) {

    }

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
