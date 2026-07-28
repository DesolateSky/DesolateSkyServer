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
import net.minestom.server.item.MaterialKeys;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.UUID;
import java.util.function.Consumer;

@NotNullByDefault
public final class VoidPig extends VoidEntity<VoidPig> {

    public static final Component ENTITY_TYPE_NAME = Component.text("Void Pig");

    private static final LootTable DROPS = new LootTable(new WeightedCollection<ItemLoot>()
            .add(1, new ItemLoot(MaterialKeys.POTATO.key(), 1, 1))
            .add(150, new ItemLoot(MaterialKeys.PORKCHOP.key(), 1, 3)));

    public VoidPig(UUID uuid, Island island, Consumer<Entity> tagApplier) {
        super(EntityType.PIG, DROPS, uuid, island, new WalkingStrategy<>(), tagApplier, ENTITY_TYPE_NAME);
    }

    public VoidPig(Island island, Consumer<Entity> tagApplier) {
        super(EntityType.PIG, DROPS, island, new WalkingStrategy<>(), tagApplier, ENTITY_TYPE_NAME);
    }

    @Override
    protected void initialize() {
        this.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.07);
        this.getAttribute(Attribute.MAX_HEALTH).setBaseValue(30);
        this.setHealth(20);
    }

    @Override
    protected void onTick(long time) {

    }
}
