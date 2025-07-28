package net.desolatesky.block.settings;

import net.desolatesky.block.BlockKeys;
import net.desolatesky.block.category.BlockCategories;
import net.desolatesky.block.handler.custom.ComposterBlockHandler;
import net.desolatesky.block.handler.custom.DebrisCatcherBlockHandler;
import net.desolatesky.block.handler.custom.SifterBlockHandler;
import net.desolatesky.item.DSItems;
import net.desolatesky.item.ItemKeys;
import net.minestom.server.instance.block.Block;

public final class DSBlockSettings {

    private DSBlockSettings() {
        throw new UnsupportedOperationException();
    }

    public static final BlockSettings DEBRIS_CATCHER = BlockSettings.builder(DebrisCatcherBlockHandler.KEY, DSItems.DEBRIS_CATCHER.create()).stateful().breakTime(1_000).blockItem(ItemKeys.DEBRIS_CATCHER).build();
    public static final BlockSettings SIFTER = BlockSettings.builder(SifterBlockHandler.KEY, DSItems.SIFTER.create()).breakTime(1_000).stateful().blockItem(ItemKeys.SIFTER).build();
    public static final BlockSettings COMPOSTER = BlockSettings.builder(ComposterBlockHandler.KEY, DSItems.COMPOSTER.create()).breakTime(1_000).stateful().blockItem(ItemKeys.COMPOSTER).categories(BlockCategories.AXE_MINEABLE).build();
    public static final BlockSettings DUST_BLOCK = BlockSettings.builder(BlockKeys.DUST_BLOCK, DSItems.DUST_BLOCK.create()).lootTable(BlockKeys.DUST_BLOCK).stateless().breakTime(500).build();

    public static final BlockSettings CRAFTING_TABLE = builder(Block.CRAFTING_TABLE).breakTime(3_000).stateless().blockItem(ItemKeys.CRAFTING_TABLE).build();
    public static final BlockSettings DIRT = builder(Block.DIRT).breakTime(1_000).stateless().blockItem(ItemKeys.DIRT).categories(BlockCategories.SHOVEL_MINEABLE, BlockCategories.DIRT).build();
    public static final BlockSettings PETRIFIED_PLANKS = builder(Block.PALE_OAK_PLANKS).breakTime(3_000).stateless().blockItem(ItemKeys.PETRIFIED_PLANKS).categories(BlockCategories.AXE_MINEABLE, BlockCategories.PLANKS).build();
    public static final BlockSettings PETRIFIED_SLAB = builder(Block.PALE_OAK_SLAB).breakTime(1_000).stateless().blockItem(ItemKeys.PETRIFIED_SLAB).categories(BlockCategories.AXE_MINEABLE, BlockCategories.WOODEN_SLABS).build();

    public static final BlockSettings WAXED_EXPOSED_COPPER_TRAPDOOR = trapdoor(Block.WAXED_EXPOSED_COPPER_TRAPDOOR).blockItem(ItemKeys.WAXED_EXPOSED_COPPER_TRAPDOOR).build();
    public static final BlockSettings WAXED_EXPOSED_CUT_COPPER_SLAB = builder(Block.WAXED_EXPOSED_CUT_COPPER_SLAB).breakTime(1_000).stateless().blockItem(ItemKeys.WAXED_EXPOSED_CUT_COPPER_SLAB).build();

    private static BlockSettings.Builder trapdoor(Block block) {
        return BlockSettings.builder(block.key(), block.registry().material()).breakTime(1_000).stateless();
    }

    private static BlockSettings.Builder builder(Block block) {
        return BlockSettings.builder(block.key(), block.registry().material());
    }

}
