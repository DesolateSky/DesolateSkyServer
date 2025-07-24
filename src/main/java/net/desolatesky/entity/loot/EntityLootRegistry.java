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

    public static EntityLootRegistry create() {
        final EntityLootRegistry entityLootRegistry = new EntityLootRegistry(new HashMap<>());
        entityLootRegistry.load();
        return entityLootRegistry;
    }

    private EntityLootRegistry(Map<Key, LootTable> entityLoots) {
        super(entityLoots);
    }

    private void load() {
        this.register(EntityKeys.DEBRIS_ENTITY, LootTable.create(Map.of(
                DebrisEntity.LOOT_GENERATOR_TYPE,
                ItemStackLootGenerator.create(
                        DebrisEntity.LOOT_GENERATOR_TYPE,
                        EntityKeys.DEBRIS_ENTITY,
                        List.of(new ItemStackLoot(DSItems.DUST, 1, 2, 5), new ItemStackLoot(DSItems.STICK, 1, 2, 5), new ItemStackLoot(DSItems.FIBER, 1, 2, 5)),
                        1,
                        2
                ))));
    }

}
