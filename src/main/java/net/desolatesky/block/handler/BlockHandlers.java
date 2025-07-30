package net.desolatesky.block.handler;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.BlockTags;
import net.desolatesky.block.DSBlock;
import net.desolatesky.block.settings.BlockSettings;
import net.kyori.adventure.key.Key;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

//public final class BlockHandlers {
//
//    public static BlockHandlers create(DesolateSkyServer server) {
//        /*server, blockLootRegistry*/
//        return new BlockHandlers();
//    }
//
//
//    private static final Map<Key, DSBlockHandler> defaultBlockHandlers = new HashMap<>();
//
//    private final Map<Key, DSBlockHandler> blockHandlers = new HashMap<>();
//
////    public static final DSBlockHandler DEBRIS_CATCHER = registerDefaultHandler(DebrisCatcherBlockEntity.HANDLER);
////    public static final DSBlockHandler SIFTER = registerDefaultHandler(SifterBlockEntity.HANDLER);
////    public static final DSBlockHandler COMPOSTER = registerDefaultHandler(ComposterBlockEntity.HANDLER);
////
////    public static final DSBlockHandler DUST_BLOCK = registerDefaultHandler(new DSBlockHandler(DSBlockSettings.DUST_BLOCK));
////    public static final DSBlockHandler PETRIFIED_PLANKS = registerDefaultHandler(DSBlockSettings.PETRIFIED_PLANKS);
////    public static final DSBlockHandler PETRIFIED_SLAB = registerDefaultHandler(DSBlockSettings.PETRIFIED_SLAB);
////    //
////    public static final DSBlockHandler WAXED_EXPOSED_CUT_COPPER_SLAB = registerDefaultHandler(DSBlockSettings.WAXED_EXPOSED_CUT_COPPER_SLAB);
////    public static final DSBlockHandler UNBREAKABLE_WAXED_EXPOSED_CUT_COPPER_SLAB = registerDefaultHandler(DSBlockSettings.UNBREAKABLE_WAXED_EXPOSED_CUT_COPPER_SLAB);
////    public static final DSBlockHandler WAXED_EXPOSED_COPPER_TRAPDOOR = registerDefaultHandler(new TrapDoorHandler(DSBlockSettings.WAXED_EXPOSED_COPPER_TRAPDOOR));
////    public static final DSBlockHandler CRAFTING_TABLE = registerDefaultHandler(new CraftingTableHandler());
////    public static final DSBlockHandler DIRT = registerDefaultHandler(DSBlockSettings.DIRT);
//
//    public void initialize() {
//        defaultBlockHandlers.values().forEach(handler -> this.blockHandlers.put(handler.key(), handler));
//    }
//
//    public @Nullable DSBlockHandler getHandler(Key key) {
//        return this.blockHandlers.get(key);
//    }
//
//    public @Nullable DSBlockHandler getHandlerForBlock(DSBlock block) {
//        final Key key = block.key();
//        return this.getHandler(key);
//    }
//
//    public @Nullable DSBlockHandler getHandlerForBlock(Block block) {
//        Key key = block.getTag(BlockTags.ID);
//        if (key == null) {
//            key = block.key();
//        }
//        return this.getHandler(key);
//    }
//
//    private static DSBlockHandler registerDefaultHandler(DSBlockHandler blockHandler) {
//        System.out.println("Registering default block handler: " + blockHandler.key() + " : " + blockHandler);
//        defaultBlockHandlers.put(blockHandler.key(), blockHandler);
//        return blockHandler;
//    }
//
//    private static DSBlockHandler registerDefaultHandler(BlockSettings blockSettings) {
//        return registerDefaultHandler(new DSBlockHandler(blockSettings));
//    }
//
//    public void registerAll(DesolateSkyServer server) {
//
//    }
//
//}
