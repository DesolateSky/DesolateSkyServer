package net.desolatesky.entity.type;

import net.desolatesky.entity.DSLivingEntity;
import net.desolatesky.entity.ai.EntityBrain;
import net.desolatesky.entity.ai.goal.PathGoal;
import net.desolatesky.island.Island;
import net.desolatesky.item.ItemIds;
import net.desolatesky.loot.ItemLoot;
import net.desolatesky.loot.LootTable;
import net.desolatesky.util.collection.WeightedCollection;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.attribute.Attribute;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@NotNullByDefault
public final class VoidSilverfish extends DSLivingEntity<VoidSilverfish> {

    private static final LootTable DROPS = new LootTable(new WeightedCollection<ItemLoot>()
            .add(1, new ItemLoot(ItemIds.SILVERFISH_CRAFTING_CATALYST, 1, 1))
            .add(10, new ItemLoot(ItemIds.SILVERFISH_EYE, 1, 2))
            .add(10, new ItemLoot(ItemIds.PEBBLE, 1, 4)));

    public VoidSilverfish(UUID uuid, Island island, Consumer<Entity> tagApplier) {
        super(EntityType.SILVERFISH, DROPS, uuid, island, tagApplier);
    }

    public VoidSilverfish(Island island, Consumer<Entity> tagApplier) {
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
    protected EntityBrain<VoidSilverfish> createBrain() {
        return new EntityBrain<>(List.of(new PathGoal<>(this)));
    }
}
