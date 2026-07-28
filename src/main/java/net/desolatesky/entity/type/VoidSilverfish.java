package net.desolatesky.entity.type;

import net.desolatesky.entity.ai.navigation.movement.WalkingStrategy;
import net.desolatesky.island.Island;
import net.desolatesky.item.ItemIds;
import net.desolatesky.loot.ItemLoot;
import net.desolatesky.loot.LootTable;
import net.desolatesky.util.collection.WeightedCollection;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.attribute.Attribute;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@NotNullByDefault
public final class VoidSilverfish extends VoidEntity<VoidSilverfish> {

    public static final Component ENTITY_TYPE_NAME = Component.text("Void Silverfish");

    private static final LootTable DROPS = new LootTable(List.of(
            new WeightedCollection<ItemLoot>()
                    .add(10, new ItemLoot(ItemIds.STONE_CHUNK, 1, 3)),
            new WeightedCollection<ItemLoot>()
                    .add(10, new ItemLoot(ItemIds.SILVERFISH_SCALE, 1, 2))
    ));

    public VoidSilverfish(UUID uuid, Island island, Consumer<Entity> tagApplier) {
        super(EntityType.SILVERFISH, DROPS, uuid, island, new WalkingStrategy<>(), tagApplier, ENTITY_TYPE_NAME);
    }

    public VoidSilverfish(Island island, Consumer<Entity> tagApplier) {
        super(EntityType.SILVERFISH, DROPS, island, new WalkingStrategy<>(), tagApplier, ENTITY_TYPE_NAME);
    }

    @Override
    protected void initialize() {
        this.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.07);
        this.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20);
        this.setHealth(20);
    }

    @Override
    protected void onTick(long time) {

    }
}
