package net.desolatesky.block.settings;

import net.desolatesky.block.BlockKeys;
import net.desolatesky.block.BlockTags;
import net.desolatesky.block.category.BlockCategories;
import net.desolatesky.block.entity.custom.SifterBlockEntity;
import net.desolatesky.item.DSItem;
import net.desolatesky.item.DSItems;
import net.desolatesky.item.ItemKeys;
import net.desolatesky.loot.generator.ItemStackLootGenerator;
import net.desolatesky.loot.table.LootTable;
import net.desolatesky.loot.type.ItemStackLoot;
import net.desolatesky.sound.Sounds;
import net.kyori.adventure.key.Key;
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
                    ItemStackLootGenerator.create(SifterBlockEntity.LOOT_GENERATOR_TYPE, List.of(
                            new ItemStackLoot(DSItems.LIFE_ESSENCE, 1, 0, 2),
                            new ItemStackLoot(DSItems.SAND_CHUNK, 50, 0, 2),
                            new ItemStackLoot(DSItems.GRAVEL_CHUNK, 50, 0, 2)
                    ), 0, 1)
            )))
            .soundSettings(builder -> builder
                    .placeSound(Sounds.BLOCK_SAND_PLACE)
                    .breakSound(Sounds.BLOCK_SAND_BREAK)
                    .digSound(Sounds.BLOCK_SAND_HIT)
            )
            .blockItem(ItemKeys.DUST_BLOCK).breakTime(1_000).build();

    public static final BlockSettings CRAFTING_TABLE = builder(Block.CRAFTING_TABLE).breakTime(PLANKS_BREAK_TIME).blockItem(ItemKeys.CRAFTING_TABLE).categories(BlockCategories.AXE_MINEABLE).build();
    public static final BlockSettings DIRT = builder(Block.DIRT).breakTime(1_000).blockItem(ItemKeys.DIRT).categories(BlockCategories.SHOVEL_MINEABLE, BlockCategories.DIRT, BlockCategories.CROP_GROWABLE).build();
    public static final BlockSettings WHEAT = builder(Block.WHEAT).breakTime(200).blockItem(ItemKeys.WHEAT_SEEDS).categories(BlockCategories.CROP).build();

    public static final BlockSettings WAXED_EXPOSED_COPPER_TRAPDOOR = trapdoor(Block.WAXED_EXPOSED_COPPER_TRAPDOOR).blockItem(ItemKeys.WAXED_EXPOSED_COPPER_TRAPDOOR).build();
    public static final BlockSettings WAXED_EXPOSED_CUT_COPPER_SLAB = builder(Block.WAXED_EXPOSED_CUT_COPPER_SLAB).breakTime(1_000).blockItem(ItemKeys.WAXED_EXPOSED_CUT_COPPER_SLAB).build();
    public static final BlockSettings UNBREAKABLE_WAXED_EXPOSED_CUT_COPPER_SLAB = builder(Block.WAXED_EXPOSED_CUT_COPPER_SLAB).unbreakable().blockItem(ItemKeys.WAXED_EXPOSED_CUT_COPPER_SLAB).build();

    // PETRIFIED WOOD
    public static final BlockSettings PETRIFIED_SAPLING = builder(BlockKeys.PETRIFIED_SAPLING, DSItems.PETRIFIED_SAPLING).breakTime(3_000).blockItem(ItemKeys.PETRIFIED_SAPLING).categories(BlockCategories.SAPLINGS).build();
    public static final BlockSettings PETRIFIED_LOG = builder(BlockKeys.PETRIFIED_LOG, DSItems.PETRIFIED_PLANKS).breakTime(PLANKS_BREAK_TIME).blockItem(ItemKeys.PETRIFIED_LOG).categories(BlockCategories.AXE_MINEABLE, BlockCategories.LOGS).tag(BlockTags.STRIPS_TO, BlockKeys.STRIPPED_PETRIFIED_LOG).build();
    public static final BlockSettings STRIPPED_PETRIFIED_LOG = builder(BlockKeys.STRIPPED_PETRIFIED_LOG, DSItems.PETRIFIED_PLANKS).breakTime(PLANKS_BREAK_TIME).blockItem(ItemKeys.STRIPPED_PETRIFIED_LOG).categories(BlockCategories.AXE_MINEABLE, BlockCategories.STRIPPED_LOGS).build();
    public static final BlockSettings PETRIFIED_LEAVES = builder(BlockKeys.PETRIFIED_LEAVES, DSItems.DEAD_LEAVES).breakTime(1_000).categories(BlockCategories.HOE_MINEABLE, BlockCategories.LEAVES).build();
    public static final BlockSettings PETRIFIED_PLANKS = builder(BlockKeys.PETRIFIED_PLANKS, DSItems.PETRIFIED_PLANKS).breakTime(PLANKS_BREAK_TIME).blockItem(ItemKeys.PETRIFIED_PLANKS).categories(BlockCategories.AXE_MINEABLE, BlockCategories.PLANKS).build();
    public static final BlockSettings PETRIFIED_SLAB = builder(BlockKeys.PETRIFIED_SLAB, DSItems.PETRIFIED_SLAB).breakTime(PLANKS_BREAK_TIME).blockItem(ItemKeys.PETRIFIED_SLAB).categories(BlockCategories.AXE_MINEABLE, BlockCategories.WOODEN_SLABS).build();


    private static BlockSettings.Builder trapdoor(Block block) {
        return BlockSettings.builder(block.key(), block.registry().material()).breakTime(1_000);
    }

    private static BlockSettings.Builder builder(Block block) {
        return BlockSettings.builder(block.key(), block.registry().material());
    }


    private static BlockSettings.Builder builder(Key key, DSItem menuItem) {
        return BlockSettings.builder(key, menuItem.create());
    }

}
