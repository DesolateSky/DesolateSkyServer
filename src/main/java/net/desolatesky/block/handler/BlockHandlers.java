package net.desolatesky.block.handler;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.BlockKeys;
import net.desolatesky.block.handler.custom.DebrisCatcherBlockHandler;
import net.desolatesky.block.handler.custom.SifterBlockHandler;
import net.desolatesky.block.handler.vanilla.CraftingTableHandler;
import net.desolatesky.block.handler.vanilla.TrapdoorHandler;
import net.desolatesky.block.loot.BlockLootRegistry;
import net.kyori.adventure.key.Key;
import net.minestom.server.MinecraftServer;
import net.minestom.server.item.Material;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Supplier;

public final class BlockHandlers {

    public static BlockHandlers load(DesolateSkyServer server, BlockLootRegistry blockLootRegistry) {
        final BlockHandlers blockHandlers = new BlockHandlers(server, blockLootRegistry);
        blockHandlers.initialize();
        blockHandlers.registerAll();
        return blockHandlers;
    }

    private final Collection<BlockHandlerSupplier<? extends DSBlockHandler>> blockHandlers = new HashSet<>();

    private final DesolateSkyServer server;
    private final BlockLootRegistry blockLootRegistry;

    private BlockHandlers(DesolateSkyServer server, BlockLootRegistry blockLootRegistry) {
        this.server = server;
        this.blockLootRegistry = blockLootRegistry;
    }

    private BlockHandlerSupplier<DSBlockHandler> defaultHandler;
    private BlockHandlerSupplier<CraftingTableHandler> craftingTableHandler;
    private BlockHandlerSupplier<TrapdoorHandler> trapdoor;
    private BlockHandlerSupplier<DebrisCatcherBlockHandler> debrisCatcher;
    private BlockHandlerSupplier<SifterBlockHandler> sifter;
    private BlockHandlerSupplier<DSBlockHandler> dustBlock;

    private void initialize() {
        this.defaultHandler = this.register(new TransientBlockHandler(this.server, Key.key("default_handler"), true));
        this.craftingTableHandler = this.register(new CraftingTableHandler(this.server, Material.CRAFTING_TABLE.key(), true));
        this.trapdoor = this.register(new TrapdoorHandler(this.server));
        this.debrisCatcher = this.register(DebrisCatcherBlockHandler.KEY, () -> new DebrisCatcherBlockHandler(this.server));
        this.sifter = this.register(SifterBlockHandler.KEY, () -> new SifterBlockHandler(this.server));
        this.dustBlock = this.register(new TransientBlockHandler(this.server, BlockKeys.DUST_BLOCK, this.blockLootRegistry.getLootTable(BlockKeys.DUST_BLOCK), true));
    }

    public BlockHandlerSupplier<DSBlockHandler> defaultHandler() {
        return this.defaultHandler;
    }

    public BlockHandlerSupplier<CraftingTableHandler> craftingTable() {
        return this.craftingTableHandler;
    }

    public BlockHandlerSupplier<TrapdoorHandler> trapdoor() {
        return this.trapdoor;
    }

    public BlockHandlerSupplier<DebrisCatcherBlockHandler> debrisCatcher() {
        return this.debrisCatcher;
    }

    public BlockHandlerSupplier<SifterBlockHandler> sifter() {
        return this.sifter;
    }

    public BlockHandlerSupplier<DSBlockHandler> dustBlock() {
        return this.dustBlock;
    }

    private <T extends DSBlockHandler> BlockHandlerSupplier<T> register(Key key, Supplier<T> supplier) {
        return this.register(new BlockHandlerSupplier<>(key, supplier));
    }

    private <T extends DSBlockHandler> BlockHandlerSupplier<T> register(BlockHandlerSupplier<T> handler) {
        this.blockHandlers.add(handler);
        return handler;
    }

    private <T extends DSBlockHandler> BlockHandlerSupplier<T> register(T handler) {
        final BlockHandlerSupplier<T> supplier = new BlockHandlerSupplier<>(handler);
        this.blockHandlers.add(supplier);
        return supplier;
    }

    public void registerAll() {
        for (final BlockHandlerSupplier<? extends DSBlockHandler> supplier : this.blockHandlers) {
            MinecraftServer.getBlockManager().registerHandler(supplier.key(), supplier);
        }
    }

}
