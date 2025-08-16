package net.desolatesky.block.settings;

import net.desolatesky.block.BlockKeys;
import net.desolatesky.block.BlockProperties;
import net.desolatesky.block.BlockTags;
import net.desolatesky.block.category.BlockCategories;
import net.desolatesky.block.entity.custom.crop.CropLootGenerator;
import net.desolatesky.block.entity.custom.powered.cable.CableSettings;
import net.desolatesky.block.entity.custom.powered.generator.PowerGeneratorSettings;
import net.desolatesky.item.DSItem;
import net.desolatesky.item.DSItems;
import net.desolatesky.item.ItemKeys;
import net.desolatesky.loot.generator.ItemStackLootGenerator;
import net.desolatesky.loot.generator.LootGeneratorTypes;
import net.desolatesky.loot.table.LootTable;
import net.desolatesky.loot.type.ItemStackLoot;
import net.desolatesky.sound.Sounds;
import net.kyori.adventure.key.Key;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.List;

public final class DSBlockSettings {

    public static final int PLANKS_BREAK_TIME = 3_000;

    private DSBlockSettings() {
        throw new UnsupportedOperationException();
    }

    public static final BlockSettings DUST_BLOCK = BlockSettings.builder(BlockKeys.DUST_BLOCK, DSItems.DUST_BLOCK.create())
            .lootTable(LootTable.builder(BlockKeys.DUST_BLOCK)
                    .generator(
                            LootGeneratorTypes.SIFTER,
                            ItemStackLootGenerator.create(List.of(
                                    new ItemStackLoot(DSItems.SAND_CHUNK, 50, 0, 2),
                                    new ItemStackLoot(DSItems.GRAVEL_CHUNK, 50, 0, 2)
                            ), 0, 1)
                    )
                    .generator(LootGeneratorTypes.BLOCK_BREAK, ItemStackLootGenerator.forDrop(DSItems.DUST_BLOCK))
                    .build()
            )
            .soundSettings(builder -> builder
                    .placeSound(Sounds.BLOCK_SAND_PLACE)
                    .breakSound(Sounds.BLOCK_SAND_BREAK)
                    .digSound(Sounds.BLOCK_SAND_HIT)
            )
            .blockItem(ItemKeys.DUST_BLOCK).breakTime(1_000).build();
    public static final BlockSettings CRAFTING_TABLE = builder(Block.CRAFTING_TABLE).breakTime(PLANKS_BREAK_TIME).blockItem(DSItems.CRAFTING_TABLE, true).categories(BlockCategories.AXE_MINEABLE).build();
    public static final BlockSettings DIRT = builder(Block.DIRT).breakTime(1_000)
            .lootTable(LootTable.builder(BlockKeys.DIRT)
                    .generator(
                            LootGeneratorTypes.SIFTER,
                            ItemStackLootGenerator.create(List.of(
                                    new ItemStackLoot(DSItems.LIFE_ESSENCE, 2, 1, 1),
                                    new ItemStackLoot(DSItems.PEBBLE, 33, 0, 2),
                                    new ItemStackLoot(DSItems.GRAVEL_CHUNK, 30, 0, 1),
                                    new ItemStackLoot(DSItems.SAND_CHUNK, 30, 0, 1),
                                    new ItemStackLoot(DSItems.DEAD_WHEAT_SEEDS, 5, 1, 1)
                            ), 0, 1)
                    )
                    .generator(LootGeneratorTypes.BLOCK_BREAK, ItemStackLootGenerator.forDrop(DSItems.DIRT))
                    .build()
            )
            .blockItem(DSItems.DIRT, false)
            .categories(BlockCategories.SHOVEL_MINEABLE, BlockCategories.DIRT, BlockCategories.CROP_GROWABLE).build();
    public static final BlockSettings SAND = builder(Block.SAND).breakTime(1_000)
            .lootTable(LootTable.builder(BlockKeys.SAND)
                    .generator(
                            LootGeneratorTypes.SIFTER,
                            ItemStackLootGenerator.create(List.of(
                                    new ItemStackLoot(DSItems.CHARCOAL, 1, 0, 1),
                                    new ItemStackLoot(DSItems.QUARTZ_CHUNK, 1, 0, 1)
                            ), 0, 1)
                    )
                    .generator(LootGeneratorTypes.BLOCK_BREAK, ItemStackLootGenerator.forDrop(DSItems.SAND))
                    .build()
            )
            .blockItem(DSItems.SAND, false)
            .categories(BlockCategories.SHOVEL_MINEABLE).build();
    public static final BlockSettings GRAVEL = builder(Block.GRAVEL).breakTime(1_000)
            .lootTable(LootTable.builder(BlockKeys.GRAVEL)
                    .generator(
                            LootGeneratorTypes.SIFTER,
                            ItemStackLootGenerator.create(List.of(
                                    new ItemStackLoot(DSItems.FLINT, 1, 0, 1)
                            ), 0, 1)
                    )
                    .generator(LootGeneratorTypes.BLOCK_BREAK, ItemStackLootGenerator.forDrop(DSItems.GRAVEL))
                    .build()
            )
            .blockItem(DSItems.GRAVEL, false)
            .categories(BlockCategories.SHOVEL_MINEABLE).build();
    public static final BlockSettings WHEAT = builder(Block.WHEAT).breakTime(200)
            .lootTable(LootTable.builder(BlockKeys.WHEAT)
                    .generator(
                            LootGeneratorTypes.BLOCK_BREAK,
                            new CropLootGenerator(
                                    ItemStackLootGenerator.create(List.of(
                                            new ItemStackLoot(DSItems.WHEAT_SEEDS, 1, 1, 1)
                                    ), 1, 1),
                                    ItemStackLootGenerator.create(List.of(
                                            new ItemStackLoot(DSItems.WHEAT_SEEDS, 1, 1, 2),
                                            new ItemStackLoot(DSItems.WHEAT, 1, 1, 1)
                                    ), 2, 2, false)
                            )
                    ).build()
            )
            .blockItem(DSItems.WHEAT_SEEDS, false)
            .categories(BlockCategories.CROP).build();

    public static final BlockSettings WAXED_EXPOSED_COPPER_TRAPDOOR = trapdoor(Block.WAXED_EXPOSED_COPPER_TRAPDOOR).build();
    public static final BlockSettings WAXED_EXPOSED_CUT_COPPER_SLAB = builder(Block.WAXED_EXPOSED_CUT_COPPER_SLAB).breakTime(1_000).blockItem(ItemKeys.WAXED_EXPOSED_CUT_COPPER_SLAB).build();
    public static final BlockSettings UNBREAKABLE_WAXED_EXPOSED_CUT_COPPER_SLAB = builder(BlockKeys.UNBREAKABLE_WAXED_EXPOSED_CUT_COPPER_SLAB, ItemStack.of(Material.WAXED_EXPOSED_CUT_COPPER_SLAB)).unbreakable().build();
    public static final BlockSettings COBBLESTONE = builder(Block.COBBLESTONE).breakTime(1_000).blockItem(DSItems.COBBLESTONE, true).categories(BlockCategories.PICKAXE_MINEABLE, BlockCategories.COBBLESTONE).build();


    // PETRIFIED WOOD
    public static final BlockSettings PETRIFIED_SAPLING = builder(BlockKeys.PETRIFIED_SAPLING, DSItems.PETRIFIED_SAPLING).breakTime(3_000).blockItem(DSItems.PETRIFIED_SAPLING, true).categories(BlockCategories.SAPLINGS).build();
    public static final BlockSettings PETRIFIED_LOG = builder(BlockKeys.PETRIFIED_LOG, DSItems.PETRIFIED_PLANKS).breakTime(PLANKS_BREAK_TIME).blockItem(DSItems.PETRIFIED_LOG, true).categories(BlockCategories.AXE_MINEABLE, BlockCategories.LOGS).tag(BlockTags.STRIPS_TO, BlockKeys.STRIPPED_PETRIFIED_LOG).build();
    public static final BlockSettings STRIPPED_PETRIFIED_LOG = builder(BlockKeys.STRIPPED_PETRIFIED_LOG, DSItems.PETRIFIED_PLANKS).breakTime(PLANKS_BREAK_TIME).blockItem(DSItems.STRIPPED_PETRIFIED_LOG, true).categories(BlockCategories.AXE_MINEABLE, BlockCategories.STRIPPED_LOGS).build();
    public static final BlockSettings PETRIFIED_LEAVES = builder(BlockKeys.PETRIFIED_LEAVES, DSItems.DEAD_LEAVES).breakTime(1_000)
            .lootTable(LootTable.builder(BlockKeys.PETRIFIED_LEAVES)
                    .generator(LootGeneratorTypes.BLOCK_BREAK, ItemStackLootGenerator.create(
                            List.of(
                                    new ItemStackLoot(DSItems.AIR, 1, 0, 0),
                                    new ItemStackLoot(DSItems.PETRIFIED_STICK, 2, 1, 1),
                                    new ItemStackLoot(DSItems.PETRIFIED_SAPLING, 1, 1, 1)
                            ), 0, 1
                    )).build()
            )
            .blockItem(DSItems.PETRIFIED_STICK, false)
            .categories(BlockCategories.HOE_MINEABLE, BlockCategories.LEAVES).build();
    public static final BlockSettings PETRIFIED_PLANKS = builder(BlockKeys.PETRIFIED_PLANKS, DSItems.PETRIFIED_PLANKS).breakTime(PLANKS_BREAK_TIME).blockItem(DSItems.PETRIFIED_PLANKS, true).categories(BlockCategories.AXE_MINEABLE, BlockCategories.PLANKS).build();
    public static final BlockSettings PETRIFIED_SLAB = builder(BlockKeys.PETRIFIED_SLAB, DSItems.PETRIFIED_SLAB).breakTime(PLANKS_BREAK_TIME).blockItem(DSItems.PETRIFIED_SAPLING, true).categories(BlockCategories.AXE_MINEABLE, BlockCategories.WOODEN_SLABS).build();


    // POWER
    public static final BlockSettings CABLE = builder(BlockKeys.CABLE, DSItems.CABLE)
            .breakTime(500)
            .blockItem(DSItems.CABLE, true)
            .tag(BlockTags.CABLE_SETTINGS, new CableSettings(Block.BLUE_TERRACOTTA, Block.CYAN_TERRACOTTA, 1_000, 10, 10))
            .build();
    public static final BlockSettings SOLAR_PANEL = builder(BlockKeys.SOLAR_PANEL, DSItems.SOLAR_PANEL)
            .breakTime(1_000)
            .blockItem(DSItems.SOLAR_PANEL, true)
            .tag(BlockTags.POWER_GENERATOR_SETTINGS, new PowerGeneratorSettings(20_000, 10, 10, 10))
            .build();
    public static final BlockSettings COBBLESTONE_GENERATOR = builder(BlockKeys.COBBLESTONE_GENERATOR, DSItems.COBBLESTONE_GENERATOR)
            .breakTime(1_000)
            .blockItem(DSItems.COBBLESTONE_GENERATOR, true)
            .tag(BlockTags.MAX_POWER, 1_000)
            .tag(BlockTags.REQUIRED_POWER, 200)
            .tag(BlockTags.TICK_INTERVAL, 10)
            .build();

    private static BlockSettings.Builder trapdoor(Block block) {
        return BlockSettings.builder(block.key(), block.registry().material()).breakTime(1_000);
    }

    private static BlockSettings.Builder builder(Block block) {
        return BlockSettings.builder(block.key(), block.registry().material());
    }

    private static BlockSettings.Builder builder(Key key, DSItem menuItem) {
        return BlockSettings.builder(key, menuItem.create());
    }

    private static BlockSettings.Builder builder(Key key, ItemStack menuItem) {
        return BlockSettings.builder(key, menuItem);
    }

}
