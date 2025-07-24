package net.desolatesky.registry;

import net.desolatesky.block.DSBlockRegistry;
import net.desolatesky.block.loot.BlockLootRegistry;
import net.desolatesky.entity.loot.EntityLootRegistry;
import net.desolatesky.item.DSItemRegistry;

public final class DSRegistries {

    private final DSBlockRegistry blockRegistry;
    private final DSItemRegistry itemRegistry;
    private final BlockLootRegistry blockLootRegistry;
    private final EntityLootRegistry entityLootRegistry;

    public DSRegistries(DSBlockRegistry blockRegistry, DSItemRegistry itemRegistry, BlockLootRegistry blockLootRegistry, EntityLootRegistry entityLootRegistry) {
        this.blockRegistry = blockRegistry;
        this.itemRegistry = itemRegistry;
        this.blockLootRegistry = blockLootRegistry;
        this.entityLootRegistry = entityLootRegistry;
    }

    public DSBlockRegistry blockRegistry() {
        return this.blockRegistry;
    }

    public DSItemRegistry itemRegistry() {
        return this.itemRegistry;
    }

    public BlockLootRegistry blockLootRegistry() {
        return this.blockLootRegistry;
    }

    public EntityLootRegistry entityLootRegistry() {
        return this.entityLootRegistry;
    }

}
