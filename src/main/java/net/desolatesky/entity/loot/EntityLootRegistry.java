package net.desolatesky.entity.loot;

import net.desolatesky.entity.EntityKeys;
import net.desolatesky.entity.type.DebrisEntity;
import net.desolatesky.item.DSItems;
import net.desolatesky.loot.generator.ItemStackLootGenerator;
import net.desolatesky.loot.table.LootTable;
import net.desolatesky.loot.table.LootTableRegistry;
import net.desolatesky.loot.type.ItemStackLoot;
import net.kyori.adventure.key.Key;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EntityLootRegistry extends LootTableRegistry {

    public static final LootTable DEBRIS_ENTITY = LootTable.create(EntityKeys.DEBRIS_ENTITY.key(), Map.of(
            DebrisEntity.LOOT_GENERATOR_TYPE,
            ItemStackLootGenerator.create(
                    DebrisEntity.LOOT_GENERATOR_TYPE,
                    List.of(
                            new ItemStackLoot(DSItems.DUST, 1, 2, 3),
                            new ItemStackLoot(DSItems.PETRIFIED_STICK, 1, 2, 3),
                            new ItemStackLoot(DSItems.FIBER, 1, 2, 3),
                            new ItemStackLoot(DSItems.DEAD_LEAVES, 1, 2, 3)
                    ),
                    1,
                    3
            )));

    public static EntityLootRegistry create() {
        return new EntityLootRegistry(new HashMap<>());
    }

    private EntityLootRegistry(Map<Key, LootTable> entityLoots) {
        super(entityLoots);
    }

    public void load() {
        this.register(DEBRIS_ENTITY);
    }

}
