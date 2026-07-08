package com.fisherl.desolatesky.entity;

import com.fisherl.desolatesky.entity.ai.EntityBrain;
import com.fisherl.desolatesky.island.Island;
import com.fisherl.desolatesky.item.definition.ItemDefinition;
import com.fisherl.desolatesky.loot.LootTable;
import com.fisherl.desolatesky.util.Pair;
import com.fisherl.desolatesky.world.DSWorld;
import com.google.errorprone.annotations.ForOverride;
import net.kyori.adventure.key.Key;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.damage.Damage;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.registry.RegistryKey;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.random.RandomGenerator;

public abstract class DSLivingEntity<T extends DSLivingEntity<T>> extends LivingEntity implements IslandEntity {

    protected final Island island;
    private final EntityBrain<T> brain;
    private final LootTable drops;
    private int ticksSinceLastDamage = -1;
    private int deadTicks = 0;

    public DSLivingEntity(EntityType entityType,
                          LootTable drops,
                          UUID uuid,
                          Island island,
                          Consumer<Entity> tagApplier) {
        super(entityType, uuid);
        this.island = island;
        this.drops = drops;
        tagApplier.accept(this);
        this.brain = this.createBrain();
    }

    public DSLivingEntity(EntityType entityType,
                          LootTable drops,
                          Island island,
                          Consumer<Entity> tagApplier
    ) {
        super(entityType);
        this.island = island;
        this.drops = drops;
        tagApplier.accept(this);
        this.brain = this.createBrain();
    }

    protected abstract void initialize();

    @ForOverride
    protected abstract EntityBrain<T> createBrain();

    @Override
    public final void tick(long time) {
        super.tick(time);
        this.brain.tick(time);
        this.ticksSinceLastDamage++;
        this.onTick(time);
        if (this.isDead() && ++this.deadTicks >= 10) {
            this.dropItems();
            this.remove(true);
        }
    }

    @Override
    public boolean damage(Damage damage) {
        if (damage.getAmount() > 0) {
            this.ticksSinceLastDamage = 0;
        }
        return super.damage(damage);
    }

    @Override
    public boolean damage(RegistryKey<DamageType> type, float amount) {
        if (amount > 0) {
            this.ticksSinceLastDamage = 0;
        }
        return super.damage(type, amount);
    }

    protected void dropItems() {
        final DSWorld world = this.world();
        final RandomGenerator randomGenerator = world.getRandomGenerator(this.position);
        final Pair<Key, Integer> droppedItem = this.drops.roll(randomGenerator);
        world.itemFactory().getItemDefinition(droppedItem.first())
                .map(ItemDefinition::defaultItemStack)
                .map(item -> item.withAmount(droppedItem.second()))
                .ifPresent(itemStack -> {
                    final ItemEntity item = new ItemEntity(itemStack);
                    item.setInstance(this.instance, this.position);
                });
    }

    protected abstract void onTick(long time);

    public int ticksSinceLastDamage() {
        return this.ticksSinceLastDamage;
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
