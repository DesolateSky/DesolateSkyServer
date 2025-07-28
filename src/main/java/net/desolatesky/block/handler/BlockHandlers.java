package net.desolatesky.block.handler;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.BlockKeys;
import net.desolatesky.block.DSBlock;
import net.desolatesky.block.handler.custom.ComposterBlockHandler;
import net.desolatesky.block.handler.custom.DebrisCatcherBlockHandler;
import net.desolatesky.block.handler.custom.SifterBlockHandler;
import net.desolatesky.block.handler.vanilla.CraftingTableHandler;
import net.desolatesky.block.handler.vanilla.TrapdoorHandler;
import net.desolatesky.block.loot.BlockLootRegistry;
import net.desolatesky.block.settings.BlockSettings;
import net.desolatesky.block.settings.DSBlockSettings;
import net.desolatesky.item.DSItems;
import net.kyori.adventure.key.Key;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public final class BlockHandlers {

    public static BlockHandlers load(DesolateSkyServer server, BlockLootRegistry blockLootRegistry) {
        /*server, blockLootRegistry*/
        return new BlockHandlers(/*server, blockLootRegistry*/);
    }


    private static final Map<Key, BlockHandlerSupplier<? extends DSBlockHandler>> DEFAULT_BLOCK_HANDLERS = new HashMap<>();

    private final Map<Key, BlockHandlerSupplier<? extends DSBlockHandler>> blockHandlers = new HashMap<>();

    public static final BlockHandlerSupplier<DebrisCatcherBlockHandler> DEBRIS_CATCHER = registerStateful(DebrisCatcherBlockHandler.KEY, DebrisCatcherBlockHandler::new);
    public static final BlockHandlerSupplier<SifterBlockHandler> SIFTER = registerStateful(SifterBlockHandler.KEY, SifterBlockHandler::new);
    public static final BlockHandlerSupplier<? extends DSBlockHandler> COMPOSTER = registerStateful(ComposterBlockHandler.KEY, ComposterBlockHandler::new);
    public static final BlockHandlerSupplier<? extends DSBlockHandler> DUST_BLOCK = registerDefaultHandler(settings(BlockKeys.DUST_BLOCK, DSItems.DUST_BLOCK.create()).lootTable(BlockKeys.DUST_BLOCK).stateless().breakTime(500));
    public static final BlockHandlerSupplier<? extends DSBlockHandler> PETRIFIED_PLANKS = registerDefaultHandler(DSBlockSettings.PETRIFIED_PLANKS);
    public static final BlockHandlerSupplier<? extends DSBlockHandler> PETRIFIED_SLAB = registerDefaultHandler(DSBlockSettings.PETRIFIED_SLAB);

    public static final BlockHandlerSupplier<? extends DSBlockHandler> WAXED_EXPOSED_CUT_COPPER_SLAB = registerDefaultHandler(DSBlockSettings.WAXED_EXPOSED_CUT_COPPER_SLAB);
    public static final BlockHandlerSupplier<? extends DSBlockHandler> UNBREAKABLE_WAXED_EXPOSED_CUT_COPPER_SLAB = registerDefaultHandler(Block.WAXED_EXPOSED_CUT_COPPER_SLAB, -1);
    public static final BlockHandlerSupplier<? extends DSBlockHandler> WAXED_EXPOSED_COPPER_TRAPDOOR = registerStateless(BlockKeys.WAXED_EXPOSED_COPPER_TRAPDOOR, server -> new TrapdoorHandler(server, BlockKeys.WAXED_EXPOSED_COPPER_TRAPDOOR, ItemStack.of(Material.WAXED_EXPOSED_COPPER_TRAPDOOR)));
    public static final BlockHandlerSupplier<? extends DSBlockHandler> CRAFTING_TABLE = registerStateless(CraftingTableHandler.KEY, CraftingTableHandler::new);
    public static final BlockHandlerSupplier<? extends DSBlockHandler> DIRT = registerDefaultHandler(DSBlockSettings.DIRT);

    public void initialize() {
        DEFAULT_BLOCK_HANDLERS.values().forEach(handler -> this.blockHandlers.put(handler.key(), handler));
    }

    public @Nullable DSBlockHandler getHandler(Key key) {
        final BlockHandler handler = MinecraftServer.getBlockManager().getHandler(key.asString());
        if (!(handler instanceof final DSBlockHandler dsBlockHandler)) {
            return null;
        }
        return dsBlockHandler;
    }

    public @Nullable DSBlockHandler getHandlerForBlock(DSBlock block) {
        final Key key = block.key();
        return this.getHandler(key);
    }

    private static BlockSettings.Builder settings(Key key, ItemStack menuItem) {
        return BlockSettings.builder(key, menuItem);
    }

    private static BlockSettings.Builder settings(Key key, Material menuMaterial) {
        return BlockSettings.builder(key, menuMaterial);
    }

    private static <T extends DSBlockHandler> BlockHandlerSupplier<T> register(Key key, BlockHandlerSupplier<T> supplier) {
        DEFAULT_BLOCK_HANDLERS.put(key, supplier);
        return supplier;
    }

    private static <T extends DSBlockHandler> BlockHandlerSupplier<T> registerStateful(Key key, Function<DesolateSkyServer, T> function) {
        return register(key, server -> () -> function.apply(server));
    }

    private static <T extends DSBlockHandler> BlockHandlerSupplier<T> registerStateless(Key key, Function<DesolateSkyServer, T> function) {
        return register(key, server -> () -> function.apply(server));
    }

    private static <T extends DSBlockHandler> BlockHandlerSupplier<T> register(Key key, Function<DesolateSkyServer, Supplier<T>> function) {
        return register(key, new BlockHandlerSupplier<>(key, function));
    }

    private static <T extends DSBlockHandler> BlockHandlerSupplier<T> register(Key key, Supplier<T> supplier) {
        return register(key, new BlockHandlerSupplier<>(key, _ -> supplier));
    }

    private static BlockHandlerSupplier<? extends DSBlockHandler> registerDefaultHandler(BlockSettings settings) {
        final Key key = settings.key();
        final BlockHandlerSupplier<? extends DSBlockHandler> existingHandler = DEFAULT_BLOCK_HANDLERS.get(key);
        if (existingHandler != null) {
            return existingHandler;
        }
        final BlockHandlerSupplier<? extends DSBlockHandler> supplier = new BlockHandlerSupplier<>(settings.key(), server -> () -> new TransientBlockHandler(server, settings));
        return register(key, supplier);
    }

    private static BlockHandlerSupplier<? extends DSBlockHandler> registerDefaultHandler(BlockSettings.Builder settings) {
        return registerDefaultHandler(settings.build());
    }

    private static BlockHandlerSupplier<? extends DSBlockHandler> registerDefaultHandler(Block block, int breakTime) {
        final Material material = Material.fromKey(block.key());
        if (material == null) {
            throw new IllegalArgumentException("Cannot create default handler for block with unknown material: " + block.key());
        }
        final ItemStack menuItem = ItemStack.of(material);
        return registerDefaultHandler(settings(block.key(), menuItem).stateless().breakTime(breakTime));
    }

    public void registerAll(DesolateSkyServer server) {
        for (final BlockHandlerSupplier<? extends DSBlockHandler> supplier : this.blockHandlers.values()) {
            MinecraftServer.getBlockManager().registerHandler(supplier.key(), supplier.getSupplier(server));
        }
    }

}
