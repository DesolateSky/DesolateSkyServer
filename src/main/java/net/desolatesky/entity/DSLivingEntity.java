package net.desolatesky.entity;

import com.google.errorprone.annotations.ForOverride;
import net.desolatesky.entity.ai.EntityBrain;
import net.desolatesky.entity.ai.navigation.EntityNavigator;
import net.desolatesky.entity.ai.navigation.MovementStrategy;
import net.desolatesky.island.Island;
import net.desolatesky.loot.LootTable;
import net.desolatesky.world.DSWorld;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.damage.Damage;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.registry.RegistryKey;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.random.RandomGenerator;

public abstract class DSLivingEntity<T extends DSLivingEntity<T>> extends EntityCreature implements IslandEntity {

    protected final Island island;
    private final EntityBrain<T> brain;
    protected final LootTable drops;
    protected final EntityNavigator<T> navigator;
    private final Component entityTypeName;
    private int ticksSinceLastDamage = -1;
    private int deadTicks = 0;

    @SuppressWarnings("unchecked")
    public DSLivingEntity(
            EntityType entityType,
            LootTable drops,
            UUID uuid,
            Island island,
            MovementStrategy<T> movementStrategy,
            Consumer<Entity> entityConfigurer,
            Component entityTypeName
    ) {
        super(entityType, uuid);
        this.island = island;
        this.drops = drops;
        entityConfigurer.accept(this);
        this.brain = this.createBrain();
        this.navigator = new EntityNavigator<>((T)this, movementStrategy);
        this.entityTypeName = entityTypeName;
        this.initialize();
    }

    @SuppressWarnings("unchecked")
    public DSLivingEntity(
            EntityType entityType,
            LootTable drops,
            Island island,
            MovementStrategy<T> movementStrategy,
            Consumer<Entity> tagApplier,
            Component entityTypeName
    ) {
        super(entityType);
        this.island = island;
        this.drops = drops;
        tagApplier.accept(this);
        this.brain = this.createBrain();
        this.navigator = new EntityNavigator<>((T)this, movementStrategy);
        this.entityTypeName = entityTypeName;
        this.initialize();
    }

    protected abstract void initialize();

    @ForOverride
    protected abstract EntityBrain<T> createBrain();

    @Override
    public final void tick(long time) {
        super.tick(time);
        this.brain.tick(time);
        this.navigator.tick();
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
        final List<ItemStack> droppedItems = this.drops.roll(randomGenerator, world.itemFactory());
        for (final ItemStack itemStack : droppedItems) {
            final ItemEntity item = new ItemEntity(itemStack);
            item.setInstance(this.instance, this.position);
        }
    }

    public EntityNavigator<T> navigator() {
        return this.navigator;
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

    public Component entityTypeName() {
        return this.entityTypeName;
    }
}
