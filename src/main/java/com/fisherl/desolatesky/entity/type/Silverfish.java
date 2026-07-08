package com.fisherl.desolatesky.entity.type;

import com.fisherl.desolatesky.entity.DSLivingEntity;
import com.fisherl.desolatesky.entity.ai.EntityBrain;
import com.fisherl.desolatesky.entity.ai.goal.PathGoal;
import com.fisherl.desolatesky.island.Island;
import com.fisherl.desolatesky.item.ItemIds;
import com.fisherl.desolatesky.loot.ItemLoot;
import com.fisherl.desolatesky.loot.LootTable;
import com.fisherl.desolatesky.util.collection.WeightedCollection;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.attribute.Attribute;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@NotNullByDefault
public final class Silverfish extends DSLivingEntity<Silverfish> {

    private static final LootTable DROPS = new LootTable(new WeightedCollection<ItemLoot>()
            .add(1, new ItemLoot(ItemIds.SILVERFISH_CRAFTING_CATALYST, 1, 1))
            .add(10, new ItemLoot(ItemIds.SILVERFISH_EYE, 1, 2))
            .add(10, new ItemLoot(ItemIds.PEBBLE, 1, 4)));

    public Silverfish(UUID uuid, Island island, Consumer<Entity> tagApplier) {
        super(EntityType.SILVERFISH, DROPS, uuid, island, tagApplier);
    }

    public Silverfish(Island island, Consumer<Entity> tagApplier) {
        super(EntityType.SILVERFISH, DROPS, island, tagApplier);
    }

    @Override
    protected void initialize() {
        this.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(5);
        this.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20);
        this.setHealth(20);
    }

    @Override
    protected void onTick(long time) {
    }

    @Override
    protected EntityBrain<Silverfish> createBrain() {
        return new EntityBrain<>(List.of(new PathGoal<>(this)));
    }
}
