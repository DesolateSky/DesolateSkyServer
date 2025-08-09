package net.desolatesky.item;

import net.desolatesky.block.BlockKeys;
import net.desolatesky.block.entity.custom.crop.Crop;
import net.desolatesky.block.entity.custom.crop.CropRarity;
import net.desolatesky.item.tool.ToolMaterial;
import net.desolatesky.item.tool.part.ToolParts;
import net.desolatesky.util.ComponentUtil;
import net.kyori.adventure.key.Key;
import net.minestom.server.component.DataComponents;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.HashMap;
import java.util.Map;

public final class DSItems {

    private static final Map<Key, DSItem> defaultItems = new HashMap<>();

    private DSItems() {
        throw new UnsupportedOperationException();
    }

    public static final DSItem AIR = DSItem.create(Material.AIR);

    // BLOCKS
    public static final DSItem DUST_BLOCK = addDefault(DSItem.create(ItemKeys.DUST_BLOCK, ItemHandlers.DUST_BLOCK, ItemStack.builder(Material.CLAY)
            .customName(ComponentUtil.noItalics("Dust Block"))
            .set(DataComponents.ITEM_MODEL, ItemKeys.DUST_BLOCK.asString())
            .set(ItemTags.BLOCK_ID, BlockKeys.DUST_BLOCK)
            .build()));
    public static final DSItem DEBRIS_CATCHER = addDefault(DSItem.create(ItemKeys.DEBRIS_CATCHER, ItemStack.builder(Material.COBWEB)
            .customName(ComponentUtil.noItalics("Debris Catcher"))
            .set(ItemTags.BLOCK_ID, BlockKeys.DEBRIS_CATCHER)
            .build()));
    public static final DSItem SIFTER = addDefault(DSItem.create(ItemKeys.SIFTER, ItemStack.builder(Material.SCAFFOLDING)
            .customName(ComponentUtil.noItalics("Sifter"))
            .set(ItemTags.BLOCK_ID, BlockKeys.SIFTER)
            .build()));
    public static final DSItem COMPOSTER = addDefault(DSItem.create(ItemKeys.COMPOSTER, ItemStack.builder(Material.COMPOSTER)
            .set(ItemTags.BLOCK_ID, BlockKeys.COMPOSTER)
            .build()));


    // ITEMS
    public static final DSItem FIBER = addDefault(DSItem.create(ItemKeys.FIBER, ItemStack.builder(Material.TALL_DRY_GRASS)
            .customName(ComponentUtil.noItalics("Fiber"))
            .build()));
    public static final DSItem FIBER_MESH = addDefault(DSItem.create(ItemKeys.FIBER_MESH, ItemStack.builder(Material.GLOW_LICHEN)
            .customName(ComponentUtil.noItalics("String Mesh"))
            .build()));
    public static final DSItem DEAD_LEAVES = addDefault(DSItem.create(ItemKeys.DEAD_LEAVES, ItemHandlers.DEAD_LEAVES, ItemStack.builder(Material.LEAF_LITTER)
            .customName(ComponentUtil.noItalics("Dead Leaves"))
            .build()));
    public static final DSItem DEAD_WHEAT_SEEDS = addDefault(DSItem.create(ItemKeys.DEAD_WHEAT_SEEDS, ItemStack.builder(Material.WHEAT_SEEDS)
            .customName(ComponentUtil.noItalics("Dead Wheat Seeds"))
            .set(ItemTags.CROP, new Crop(1, CropRarity.COMMON))
            .set(DataComponents.ITEM_MODEL, ItemKeys.DEAD_WHEAT_SEEDS.asString())
            .build()));
    public static final DSItem WHEAT_SEEDS = addDefault(DSItem.create(ItemKeys.WHEAT_SEEDS, ItemStack.builder(Material.WHEAT_SEEDS)
            .set(ItemTags.CROP, new Crop(7, CropRarity.COMMON))
            .set(ItemTags.BLOCK_ID, BlockKeys.WHEAT)
            .build()));
    public static final DSItem WHEAT = addDefault(DSItem.create(ItemKeys.WHEAT, ItemStack.builder(Material.WHEAT)
            .customName(ComponentUtil.noItalics("Wheat"))
            .set(ItemTags.BLOCK_ID, BlockKeys.WHEAT)
            .build()));
    public static final DSItem PEBBLE = addDefault(DSItem.create(ItemKeys.PEBBLE, ItemStack.builder(Material.PAPER)
            .customName(ComponentUtil.noItalics("Pebble"))
            .set(DataComponents.ITEM_MODEL, ItemKeys.PEBBLE.asString())
            .build()));

    // DUST
    public static final DSItem DUST = addDefault(DSItem.create(ItemKeys.DUST, ItemStack.builder(Material.SUGAR)
            .customName(ComponentUtil.noItalics("Dust"))
            .set(DataComponents.ITEM_MODEL, ItemKeys.DUST.asString())
            .build()));
    public static final DSItem DIRT_CHUNK = addDefault(DSItem.create(ItemKeys.DIRT_CHUNK, ItemStack.builder(Material.SUGAR)
            .customName(ComponentUtil.noItalics("Dirt Chunk"))
            .set(DataComponents.ITEM_MODEL, ItemKeys.DIRT_CHUNK.asString())
            .build()));
    public static final DSItem SAND_CHUNK = addDefault(DSItem.create(ItemKeys.SAND_CHUNK, ItemStack.builder(Material.SUGAR)
            .customName(ComponentUtil.noItalics("Sand Chunk"))
            .set(DataComponents.ITEM_MODEL, ItemKeys.SAND_CHUNK.asString())
            .build()));
    public static final DSItem GRAVEL_CHUNK = addDefault(DSItem.create(ItemKeys
            .GRAVEL_CHUNK, ItemStack.builder(Material.SUGAR)
            .customName(ComponentUtil.noItalics("Gravel Chunk"))
            .set(DataComponents.ITEM_MODEL, ItemKeys.GRAVEL_CHUNK.asString())
            .build()));
    public static final DSItem CLAY_BALL = addDefault(DSItem.create(ItemKeys.CLAY_BALL, ItemStack.builder(Material.CLAY_BALL)
            .customName(ComponentUtil.noItalics("Clay Ball"))
            .build()));
    public static final DSItem COPPER_DUST = addDefault(DSItem.create(ItemKeys.COPPER_DUST, ItemStack.builder(Material.SUGAR)
            .customName(ComponentUtil.noItalics("Copper Dust"))
            .set(DataComponents.ITEM_MODEL, ItemKeys.COPPER_DUST.asString())
            .build()));
    public static final DSItem QUARTZ_CHUNK = addDefault(DSItem.create(ItemKeys.QUARTZ_CHUNK, ItemStack.builder(Material.QUARTZ)
            .customName(ComponentUtil.noItalics("Quartz Chunk"))
            .build()));

    // ESSENCE
    public static final DSItem LIFE_ESSENCE = addDefault(DSItem.create(ItemKeys.LIFE_ESSENCE, ItemStack.builder(Material.GLOW_INK_SAC)
            .customName(ComponentUtil.noItalics("Life Essence"))
            .set(DataComponents.ITEM_MODEL, ItemKeys.LIFE_ESSENCE.asString())
            .build()));

    // PETRIFIED WOOD
    public static final DSItem PETRIFIED_SAPLING = addDefault(DSItem.create(ItemKeys.PETRIFIED_SAPLING, ItemStack.builder(Material.PALE_OAK_SAPLING)
            .customName(ComponentUtil.noItalics("Petrified Sapling"))
            .set(ItemTags.CROP, new Crop(1, CropRarity.COMMON))
            .set(ItemTags.BLOCK_ID, BlockKeys.PETRIFIED_SAPLING)
            .build()));
    public static final DSItem PETRIFIED_LOG = addDefault(DSItem.create(ItemKeys.PETRIFIED_LOG, ItemStack.builder(Material.PALE_OAK_LOG)
            .customName(ComponentUtil.noItalics("Petrified Log"))
            .set(ItemTags.BLOCK_ID, BlockKeys.PETRIFIED_LOG)
            .build()));
    public static final DSItem STRIPPED_PETRIFIED_LOG = addDefault(DSItem.create(ItemKeys.STRIPPED_PETRIFIED_LOG, ItemStack.builder(Material.STRIPPED_PALE_OAK_LOG)
            .customName(ComponentUtil.noItalics("Stripped Petrified Log"))
            .set(ItemTags.BLOCK_ID, BlockKeys.STRIPPED_PETRIFIED_LOG)
            .build()));
    public static final DSItem PETRIFIED_PLANKS = addDefault(DSItem.create(ItemKeys.PETRIFIED_PLANKS, ItemHandlers.PETRIFIED_PLANKS, ItemStack.builder(Material.PALE_OAK_PLANKS)
            .customName(ComponentUtil.noItalics("Petrified Planks"))
            .set(ItemTags.BLOCK_ID, BlockKeys.PETRIFIED_PLANKS.key())
            .build()));
    public static final DSItem PETRIFIED_SLAB = addDefault(DSItem.create(ItemKeys.PETRIFIED_SLAB, ItemHandlers.PETRIFIED_SLAB, ItemStack.builder(Material.PALE_OAK_SLAB)
            .customName(ComponentUtil.noItalics("Petrified Slab"))
            .set(ItemTags.BLOCK_ID, BlockKeys.PETRIFIED_SLAB.key())
            .build()));
    public static final DSItem PETRIFIED_STICK = addDefault(DSItem.create(ItemKeys.PETRIFIED_STICK, ItemHandlers.PETRIFIED_STICK, ItemStack.builder(Material.STICK)
            .customName(ComponentUtil.noItalics("Petrified Stick"))
            .set(DataComponents.ITEM_MODEL, ItemKeys.PETRIFIED_STICK.asString())
            .build()));

    // WOODEN_TOOLS
    public static final DSItem WOODEN_AXE_HEAD = addDefault(DSItem.create(ItemKeys.WOODEN_AXE_HEAD, ItemStack.builder(Material.PAPER)
            .set(DataComponents.MAX_STACK_SIZE, 1)
            .set(DataComponents.ITEM_MODEL, ItemKeys.WOODEN_AXE_HEAD.asString())
            .customName(ComponentUtil.noItalics("Wooden Axe Head"))
            .set(ItemTags.TOOL_PART, ToolParts.AXE_HEAD.key())
            .set(ItemTags.TOOL_MATERIAL, ToolMaterial.WOODEN)
            .set(ItemTags.TOOL_MODEL, Key.key("wooden_axe"))
            .build()));
    public static final DSItem WOODEN_TOOL_HANDLE = addDefault(DSItem.create(ItemKeys.WOODEN_TOOL_HANDLE, ItemStack.builder(Material.STICK)
            .set(DataComponents.MAX_STACK_SIZE, 1)
            .customName(ComponentUtil.noItalics("Wooden Handle"))
            .set(ItemTags.TOOL_PART, ToolParts.HANDLE.key())
            .set(ItemTags.TOOL_MATERIAL, ToolMaterial.WOODEN)
            .build()));
    public static final DSItem WOODEN_TOOL_BINDING = addDefault(DSItem.create(ItemKeys.WOODEN_TOOL_BINDING, ItemStack.builder(Material.PAPER)
            .set(DataComponents.MAX_STACK_SIZE, 1)
            .set(DataComponents.ITEM_MODEL, ItemKeys.WOODEN_TOOL_BINDING.asString())
            .customName(ComponentUtil.noItalics("Wooden Binding"))
            .set(ItemTags.TOOL_PART, ToolParts.BINDING.key())
            .set(ItemTags.TOOL_MATERIAL, ToolMaterial.WOODEN)
            .build()));

    // TOOLS
    public static final DSItem AXE = addDefault(DSItem.create(ItemKeys.AXE, ItemHandlers.AXE_HANDLER, ItemStack.builder(Material.PAPER)
            .customName(ComponentUtil.noItalics("Wooden Axe"))
            .build()));

    // POWER
    public static final DSItem CABLE = addDefault(DSItem.create(ItemKeys.CABLE, ItemStack.builder(Material.PAPER)
            .customName(ComponentUtil.noItalics("Cable"))
            .set(DataComponents.ITEM_MODEL, ItemKeys.CABLE.asString())
            .set(ItemTags.BLOCK_ID, BlockKeys.CABLE)
            .build()));

    // vanilla
    public static final DSItem CRAFTING_TABLE = addDefault(DSItem.create(ItemStack.builder(Material.CRAFTING_TABLE).set(ItemTags.BLOCK_ID, BlockKeys.CRAFTING_TABLE).build()));
    public static final DSItem DIRT = addDefault(DSItem.create(ItemKeys.DIRT, ItemStack.builder(Material.DIRT).set(ItemTags.BLOCK_ID, BlockKeys.DIRT).build()));
    public static final DSItem SAND = addDefault(DSItem.create(ItemKeys.SAND, ItemStack.builder(Material.SAND).set(ItemTags.BLOCK_ID, BlockKeys.SAND).build()));
    public static final DSItem GRAVEL = addDefault(DSItem.create(ItemKeys.GRAVEL, ItemStack.builder(Material.GRAVEL).set(ItemTags.BLOCK_ID, BlockKeys.GRAVEL).build()));
    public static final DSItem CLAY = addDefault(DSItem.create(ItemKeys.CLAY, ItemStack.builder(Material.CLAY_BALL).set(ItemTags.BLOCK_ID, BlockKeys.CLAY).build()));
    public static final DSItem FLINT = addDefault(DSItem.create(ItemKeys.FLINT, ItemStack.builder(Material.FLINT).build()));
    public static final DSItem CHARCOAL = addDefault(DSItem.create(ItemKeys.CHARCOAL, ItemStack.builder(Material.CHARCOAL).build()));

    public static void register(DSItemRegistry itemRegistry) {
        defaultItems.values().forEach(itemRegistry::register);
    }

    private static DSItem addDefault(DSItem dsItem) {
        defaultItems.putIfAbsent(dsItem.key(), dsItem);
        return dsItem;
    }

    public static boolean isBlock(ItemStack itemStack) {
        final Key id = itemStack.getTag(ItemTags.BLOCK_ID);
        if (id == null) {
            return itemStack.material().isBlock();
        }
        return true;
    }

}
