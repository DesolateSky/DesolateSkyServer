package net.desolatesky.block.settings;

import net.desolatesky.block.BlockKeys;
import net.desolatesky.block.category.BlockCategories;
import net.desolatesky.block.entity.custom.ComposterBlockEntity;
import net.desolatesky.block.entity.custom.SifterBlockEntity;
import net.desolatesky.item.DSItems;
import net.desolatesky.item.ItemKeys;
import net.desolatesky.loot.generator.ItemStackLootGenerator;
import net.desolatesky.loot.table.LootTable;
import net.desolatesky.loot.type.ItemStackLoot;
import net.minestom.server.instance.block.Block;

import java.util.List;
import java.util.Map;

public final class DSBlockSettings {

    public static final int PLANKS_BREAK_TIME = 3_000;

    private DSBlockSettings() {
        throw new UnsupportedOperationException();
    }

    public static final BlockSettings DUST_BLOCK = BlockSettings.builder(BlockKeys.DUST_BLOCK, DSItems.DUST_BLOCK.create())
            .lootTable(LootTable.create(BlockKeys.DUST_BLOCK, Map.of(
                            SifterBlockEntity.LOOT_GENERATOR_TYPE,
                            ItemStackLootGenerator.create(SifterBlockEntity.LOOT_GENERATOR_TYPE, List.of(new ItemStackLoot(DSItems.COPPER_DUST, 1, 0, 2)), 1, 3)
                    )))
            .blockItem(ItemKeys.DUST_BLOCK).breakTime(500).build();

    public static final BlockSettings CRAFTING_TABLE = builder(Block.CRAFTING_TABLE).breakTime(PLANKS_BREAK_TIME).blockItem(ItemKeys.CRAFTING_TABLE).categories(BlockCategories.AXE_MINEABLE).build();
    public static final BlockSettings DIRT = builder(Block.DIRT).breakTime(1_000).blockItem(ItemKeys.DIRT).categories(BlockCategories.SHOVEL_MINEABLE, BlockCategories.DIRT).build();
    public static final BlockSettings PETRIFIED_PLANKS = builder(Block.PALE_OAK_PLANKS).breakTime(PLANKS_BREAK_TIME).blockItem(ItemKeys.PETRIFIED_PLANKS).categories(BlockCategories.AXE_MINEABLE, BlockCategories.PLANKS).build();
    public static final BlockSettings PETRIFIED_SLAB = builder(Block.PALE_OAK_SLAB).breakTime(PLANKS_BREAK_TIME).blockItem(ItemKeys.PETRIFIED_SLAB).categories(BlockCategories.AXE_MINEABLE, BlockCategories.WOODEN_SLABS).build();

    public static final BlockSettings WAXED_EXPOSED_COPPER_TRAPDOOR = trapdoor(Block.WAXED_EXPOSED_COPPER_TRAPDOOR).blockItem(ItemKeys.WAXED_EXPOSED_COPPER_TRAPDOOR).build();
    public static final BlockSettings WAXED_EXPOSED_CUT_COPPER_SLAB = builder(Block.WAXED_EXPOSED_CUT_COPPER_SLAB).breakTime(1_000).blockItem(ItemKeys.WAXED_EXPOSED_CUT_COPPER_SLAB).build();
    public static final BlockSettings UNBREAKABLE_WAXED_EXPOSED_CUT_COPPER_SLAB = builder(Block.WAXED_EXPOSED_CUT_COPPER_SLAB).unbreakable().blockItem(ItemKeys.WAXED_EXPOSED_CUT_COPPER_SLAB).build();

    private static BlockSettings.Builder trapdoor(Block block) {
        return BlockSettings.builder(block.key(), block.registry().material()).breakTime(1_000);
    }

    private static BlockSettings.Builder builder(Block block) {
        return BlockSettings.builder(block.key(), block.registry().material());
    }

}
