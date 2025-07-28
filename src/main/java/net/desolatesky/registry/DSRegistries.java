package net.desolatesky.registry;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.DSBlockRegistry;
import net.desolatesky.block.DSBlocks;
import net.desolatesky.block.loot.BlockLootRegistry;
import net.desolatesky.entity.loot.EntityLootRegistry;
import net.desolatesky.item.DSItemRegistry;
import net.desolatesky.item.DSItems;
import net.desolatesky.item.loot.ItemLootRegistry;

public final class DSRegistries {

    private final DSBlockRegistry blockRegistry;
    private final DSItemRegistry itemRegistry;
    private final BlockLootRegistry blockLootRegistry;
    private final ItemLootRegistry itemLootRegistry;
    private final EntityLootRegistry entityLootRegistry;

    public DSRegistries(
            DSBlockRegistry blockRegistry,
            DSItemRegistry itemRegistry,
            BlockLootRegistry blockLootRegistry,
            ItemLootRegistry itemLootRegistry,
            EntityLootRegistry entityLootRegistry
    ) {
        this.blockRegistry = blockRegistry;
        this.itemRegistry = itemRegistry;
        this.blockLootRegistry = blockLootRegistry;
        this.itemLootRegistry = itemLootRegistry;
        this.entityLootRegistry = entityLootRegistry;
    }

    public void registerAll(DesolateSkyServer server) {
        this.blockRegistry.blockHandlers().initialize();
        this.blockRegistry.blockHandlers().registerAll(server);

        this.blockLootRegistry.load();
        this.itemLootRegistry.load();
        this.entityLootRegistry.load();

        DSItems.register(this.itemRegistry);
        this.blockRegistry.blocks().register(this.blockRegistry);
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

    public ItemLootRegistry itemLootRegistry() {
        return this.itemLootRegistry;
    }

    public EntityLootRegistry entityLootRegistry() {
        return this.entityLootRegistry;
    }

}
