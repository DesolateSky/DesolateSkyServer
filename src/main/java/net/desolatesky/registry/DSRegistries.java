package net.desolatesky.registry;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.DSBlockRegistry;
import net.desolatesky.entity.loot.EntityLootRegistry;
import net.desolatesky.item.DSItemRegistry;
import net.desolatesky.item.DSItems;
import net.desolatesky.item.loot.ItemLootRegistry;
import net.desolatesky.item.tool.registry.ToolPartRegistry;
import net.minestom.server.MinecraftServer;

public final class DSRegistries {

    private final DSBlockRegistry blockRegistry;
    private final DSItemRegistry itemRegistry;
    private final ItemLootRegistry itemLootRegistry;
    private final EntityLootRegistry entityLootRegistry;
    private final ToolPartRegistry toolPartRegistry;

    public DSRegistries(
            DSBlockRegistry blockRegistry,
            DSItemRegistry itemRegistry,
            ItemLootRegistry itemLootRegistry,
            EntityLootRegistry entityLootRegistry,
            ToolPartRegistry toolPartRegistry
    ) {
        this.blockRegistry = blockRegistry;
        this.itemRegistry = itemRegistry;
        this.itemLootRegistry = itemLootRegistry;
        this.entityLootRegistry = entityLootRegistry;
        this.toolPartRegistry = toolPartRegistry;
    }

    public void registerAll(DesolateSkyServer server) {
        this.itemLootRegistry.load();
        this.entityLootRegistry.load();

        DSItems.register(this.itemRegistry);
        this.blockRegistry.blockEntities().register(MinecraftServer.getBlockManager(), server);
        this.blockRegistry.blocks().register(this.blockRegistry);

        this.toolPartRegistry.load();
    }

    public DSBlockRegistry blockRegistry() {
        return this.blockRegistry;
    }

    public DSItemRegistry itemRegistry() {
        return this.itemRegistry;
    }

    public ItemLootRegistry itemLootRegistry() {
        return this.itemLootRegistry;
    }

    public EntityLootRegistry entityLootRegistry() {
        return this.entityLootRegistry;
    }

    public ToolPartRegistry toolPartRegistry() {
        return this.toolPartRegistry;
    }

}
