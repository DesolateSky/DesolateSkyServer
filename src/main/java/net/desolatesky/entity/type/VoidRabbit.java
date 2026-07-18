
package net.desolatesky.entity.type;

import net.desolatesky.entity.DSLivingEntity;
import net.desolatesky.entity.ai.EntityBrain;
import net.desolatesky.entity.ai.goal.PathGoal;
import net.desolatesky.island.Island;
import net.desolatesky.loot.ItemLoot;
import net.desolatesky.loot.LootTable;
import net.desolatesky.util.collection.WeightedCollection;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@NotNullByDefault
public final class VoidRabbit extends DSLivingEntity<VoidRabbit> {

    private static final LootTable DROPS = new LootTable(
            List.of(
                    new WeightedCollection<ItemLoot>()
                            .add(75, new ItemLoot(Material.RABBIT.key(), 1, 1))
                            .add(75, new ItemLoot(Material.RABBIT_HIDE.key(), 1, 3)),
                    new WeightedCollection<ItemLoot>()
                            .add(1, new ItemLoot(Material.CARROT.key(), 1, 1))
                            .add(99, new ItemLoot(Material.AIR.key(), 1, 1))
            )
    );


    public VoidRabbit(UUID uuid, Island island, Consumer<Entity> tagApplier) {
        super(EntityType.RABBIT, DROPS, uuid, island, tagApplier);
    }

    public VoidRabbit(Island island, Consumer<Entity> tagApplier) {
        super(EntityType.RABBIT, DROPS, island, tagApplier);
    }

    @Override
    protected void initialize() {
        this.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(5);
        this.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20);
        this.setHealth(20);
    }

    @Override
    protected void onTick(long time) {
        if (!this.isGlowing()) {
            this.setGlowing(true);
        }
    }

    @Override
    protected EntityBrain<VoidRabbit> createBrain() {
        return new EntityBrain<>(List.of(new PathGoal<>(this)));
    }
}
