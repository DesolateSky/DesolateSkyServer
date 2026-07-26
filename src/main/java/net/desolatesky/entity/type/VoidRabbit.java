
package net.desolatesky.entity.type;

import net.desolatesky.entity.ai.navigation.movement.HoppingStrategy;
import net.desolatesky.island.Island;
import net.desolatesky.loot.ItemLoot;
import net.desolatesky.loot.LootTable;
import net.desolatesky.util.collection.WeightedCollection;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@NotNullByDefault
public final class VoidRabbit extends VoidEntity<VoidRabbit> {

    public static final Component ENTITY_TYPE_NAME = Component.text("Void Rabbit");

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
        super(EntityType.RABBIT, DROPS, uuid, island, new HoppingStrategy<>(4), tagApplier, ENTITY_TYPE_NAME);
    }

    public VoidRabbit(Island island, Consumer<Entity> tagApplier) {
        super(EntityType.RABBIT, DROPS, island, new HoppingStrategy<>(4), tagApplier, ENTITY_TYPE_NAME);
    }

    @Override
    protected void initialize() {
        this.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.1);
        this.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20);
        this.setHealth(20);
        this.setGlowing(true);
    }

    @Override
    protected void onTick(long time) {
        if (!this.isGlowing()) {
        }
    }
}
