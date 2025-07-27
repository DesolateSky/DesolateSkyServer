package net.desolatesky.block.handler;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.BlockKeys;
import net.desolatesky.block.handler.custom.DebrisCatcherBlockHandler;
import net.desolatesky.block.handler.custom.SifterBlockHandler;
import net.desolatesky.block.handler.vanilla.CraftingTableHandler;
import net.desolatesky.block.handler.vanilla.TrapdoorHandler;
import net.desolatesky.block.loot.BlockLootRegistry;
import net.desolatesky.block.settings.BlockSettings;
import net.desolatesky.block.settings.DSBlockSettings;
import net.desolatesky.item.DSItems;
import net.desolatesky.item.ItemKeys;
import net.kyori.adventure.key.Key;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.beans.Transient;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public final class BlockHandlers {

    public static BlockHandlers load(DesolateSkyServer server, BlockLootRegistry blockLootRegistry) {
        final BlockHandlers blockHandlers = new BlockHandlers(server, blockLootRegistry);
        blockHandlers.initialize();
        blockHandlers.registerAll();
        return blockHandlers;
    }

    private final Map<Key, BlockHandlerSupplier<? extends DSBlockHandler>> blockHandlers = new HashMap<>();

    private final DesolateSkyServer server;
    private final BlockLootRegistry blockLootRegistry;

    private BlockHandlers(DesolateSkyServer server, BlockLootRegistry blockLootRegistry) {
        this.server = server;
        this.blockLootRegistry = blockLootRegistry;
    }

    private final Map<Key, BlockHandlerSupplier<TrapdoorHandler>> trapdoors = new HashMap<>();
    private BlockHandlerSupplier<DebrisCatcherBlockHandler> debrisCatcher;
    private BlockHandlerSupplier<SifterBlockHandler> sifter;
    private BlockHandlerSupplier<? extends DSBlockHandler> dustBlock;

    private BlockHandlerSupplier<CraftingTableHandler> craftingTable;
    private BlockHandlerSupplier<? extends DSBlockHandler> oakPlanks;
    private BlockHandlerSupplier<? extends DSBlockHandler> waxedExposedCutCopperSlab;
    private BlockHandlerSupplier<? extends DSBlockHandler> unbreakableWaxedExposedCutCopperSlab;

    private void initialize() {
        this.debrisCatcher = this.register(DebrisCatcherBlockHandler.KEY, () -> new DebrisCatcherBlockHandler(this.server));
        this.sifter = this.register(SifterBlockHandler.KEY, () -> new SifterBlockHandler(this.server));
        this.dustBlock = this.createDefaultHandler(settings(BlockKeys.DUST_BLOCK, DSItems.DUST_BLOCK.create()).lootTable(BlockKeys.DUST_BLOCK).stateless().breakTime(500));

        this.craftingTable = this.register(new CraftingTableHandler(this.server));
        this.oakPlanks = this.createDefaultHandler(DSBlockSettings.OAK_PLANKS);
        this.waxedExposedCutCopperSlab = this.createDefaultHandler(DSBlockSettings.WAXED_EXPOSED_CUT_COPPER_SLAB);
        this.unbreakableWaxedExposedCutCopperSlab = this.createDefaultHandler(Block.WAXED_EXPOSED_CUT_COPPER_SLAB, -1);
    }

    public BlockHandlerSupplier<CraftingTableHandler> craftingTable() {
        return this.craftingTable;
    }

    public BlockHandlerSupplier<TrapdoorHandler> getTrapdoor(Key key) {
        return this.trapdoors.computeIfAbsent(key, k -> this.register(new TrapdoorHandler(this.server, k, ItemStack.of(Objects.requireNonNull(Material.fromKey(key), "Unknown material for trapdoor: " + key)))));
    }

    public BlockHandlerSupplier<DebrisCatcherBlockHandler> debrisCatcher() {
        return this.debrisCatcher;
    }

    public BlockHandlerSupplier<SifterBlockHandler> sifter() {
        return this.sifter;
    }

    public BlockHandlerSupplier<? extends DSBlockHandler> dustBlock() {
        return this.dustBlock;
    }

    public BlockHandlerSupplier<? extends DSBlockHandler> oakPlanks() {
        return this.oakPlanks;
    }

    public BlockHandlerSupplier<? extends DSBlockHandler> waxedExposedCutCopperSlab() {
        return this.waxedExposedCutCopperSlab;
    }

    public BlockHandlerSupplier<? extends DSBlockHandler> unbreakableWaxedExposedCutCopperSlab() {
        return this.unbreakableWaxedExposedCutCopperSlab;
    }

    private BlockHandlerSupplier<? extends DSBlockHandler> createDefaultHandler(Key key, ItemStack menuItem, int breakTime) {
        final BlockHandlerSupplier<? extends DSBlockHandler> existingHandler = this.blockHandlers.get(key);
        if (existingHandler != null) {
            return existingHandler;
        }
        return this.createDefaultHandler(settings(key, menuItem).stateless().breakTime(breakTime).build());
    }

    private BlockHandlerSupplier<? extends DSBlockHandler> createDefaultHandler(Block block, ItemStack menuItem, int breakTime) {
        return this.createDefaultHandler(block.key(), menuItem, breakTime);
    }

    private BlockHandlerSupplier<? extends DSBlockHandler> createDefaultHandler(Block block, int breakTime) {
        final Material material = Material.fromKey(block.key());
        if (material == null) {
            throw new IllegalArgumentException("Cannot create default handler for block with unknown material: " + block.key());
        }
        return this.createDefaultHandler(block.key(), ItemStack.of(material), breakTime);
    }

    private BlockHandlerSupplier<? extends DSBlockHandler> createDefaultHandler(BlockSettings.Builder settings) {
        return this.createDefaultHandler(settings.build());
    }

    private BlockHandlerSupplier<? extends DSBlockHandler> createDefaultHandler(BlockSettings settings) {
        final Key key = settings.key();
        final BlockHandlerSupplier<? extends DSBlockHandler> existingHandler = this.blockHandlers.get(key);
        if (existingHandler != null) {
            return existingHandler;
        }
        final TransientBlockHandler handler = new TransientBlockHandler(this.server, settings);
        return this.register(handler);
    }

    private <T extends DSBlockHandler> BlockHandlerSupplier<T> register(Key key, Supplier<T> supplier) {
        return this.register(new BlockHandlerSupplier<>(key, supplier));
    }

    private <T extends DSBlockHandler> BlockHandlerSupplier<T> register(BlockHandlerSupplier<T> handler) {
        this.blockHandlers.put(handler.key(), handler);
        return handler;
    }

    private <T extends DSBlockHandler> BlockHandlerSupplier<T> register(T handler) {
        final BlockHandlerSupplier<T> supplier = new BlockHandlerSupplier<>(handler);
        this.blockHandlers.put(supplier.key(), supplier);
        return supplier;
    }

    private static BlockSettings.Builder settings(Key key, ItemStack menuItem) {
        return BlockSettings.builder(key, menuItem);
    }

    private static BlockSettings.Builder settings(Key key, Material menuMaterial) {
        return BlockSettings.builder(key, menuMaterial);
    }

    public void registerAll() {
        for (final BlockHandlerSupplier<? extends DSBlockHandler> supplier : this.blockHandlers.values()) {
            MinecraftServer.getBlockManager().registerHandler(supplier.key(), supplier);
        }
    }

}
