package net.desolatesky.item;

import net.desolatesky.util.Namespace;
import net.kyori.adventure.key.Key;
import net.minestom.server.item.Material;

public final class ItemKeys {

    private ItemKeys() {
        throw new UnsupportedOperationException();
    }

    // BLOCKS
    public static final Key DUST_BLOCK = Namespace.key("dust_block");
    public static final Key DEBRIS_CATCHER = Namespace.key("debris_catcher");
    public static final Key SIFTER = Namespace.key("sifter");
    public static final Key COMPOSTER = key(Material.COMPOSTER);
    public static final Key DIRT = key(Material.DIRT);
    public static final Key SAND = key(Material.SAND);
    public static final Key GRAVEL = key(Material.GRAVEL);
    public static final Key CLAY = Namespace.key("clay");

    // ITEMS
    public static final Key FIBER = Namespace.key("fiber");
    public static final Key FIBER_MESH = Namespace.key("fiber_mesh");
    public static final Key DEAD_LEAVES = Namespace.key("dead_leaves");
    public static final Key DEAD_WHEAT_SEEDS = Namespace.key("dead_wheat_seeds");
    public static final Key WHEAT_SEEDS = key(Material.WHEAT_SEEDS);
    public static final Key WHEAT = key(Material.WHEAT);
    public static final Key PEBBLE = Namespace.key("pebble");

    // DUST
    public static final Key DIRT_CHUNK = Namespace.key("dirt_chunk");
    public static final Key SAND_CHUNK = Namespace.key("sand_chunk");
    public static final Key GRAVEL_CHUNK = Namespace.key("gravel_chunk");
    public static final Key CLAY_BALL = key(Material.CLAY_BALL);
    public static final Key DUST = Namespace.key("dust");
    public static final Key COPPER_DUST = Namespace.key("copper_dust");
    public static final Key QUARTZ_CHUNK = Namespace.key("quartz_chunk");

    // ESSENCE
    public static final Key LIFE_ESSENCE = Namespace.key("life_essence");

    // PETRIFIED WOOD
    public static final Key PETRIFIED_SAPLING = Namespace.key("petrified_sapling");
    public static final Key PETRIFIED_LOG = Namespace.key("petrified_log");
    public static final Key STRIPPED_PETRIFIED_LOG = Namespace.key("stripped_petrified_log");
    public static final Key PETRIFIED_STICK = Namespace.key("petrified_stick");
    public static final Key PETRIFIED_PLANKS = Namespace.key("petrified_planks");
    public static final Key PETRIFIED_SLAB = Namespace.key("petrified_slab");

    // VANILLA
    public static final Key CRAFTING_TABLE = key(Material.CRAFTING_TABLE);
    public static final Key STRING = key(Material.STRING);
    public static final Key FLINT = key(Material.FLINT);
    public static final Key CHARCOAL = key(Material.CHARCOAL);

    public static final Key WAXED_EXPOSED_COPPER_TRAPDOOR = key(Material.WAXED_EXPOSED_COPPER_TRAPDOOR);
    public static final Key WAXED_EXPOSED_CUT_COPPER_SLAB = key(Material.WAXED_EXPOSED_CUT_COPPER_SLAB);

    //  TOOLS
    public static final Key AXE = Namespace.key("axe");
    public static final Key PICKAXE = Namespace.key("pickaxe");
    public static final Key HOE = Namespace.key("hoe");
    public static final Key SHOVEL = Namespace.key("shovel");
    public static final Key SICKLE = Namespace.key("sickle");
    public static final Key SCYTHE = Namespace.key("scythe");
    public static final Key BROAD_AXE = Namespace.key("broad_axe");
    public static final Key MACE = Namespace.key("mace");
    public static final Key HAMMER = Namespace.key("hammer");
    public static final Key SWORD = Namespace.key("sword");
    public static final Key BOW = Namespace.key("bow");
    public static final Key ARROW = Namespace.key("arrow");

    // TOOL_PARTS
    public static final Key WOODEN_AXE_HEAD = Namespace.key("wooden_axe_head");
    public static final Key WOODEN_PICKAXE_HEAD = Namespace.key("wooden_pickaxe_head");
    public static final Key WOODEN_HOE_HEAD = Namespace.key("wooden_hoe_head");
    public static final Key WOODEN_SHOVEL_HEAD = Namespace.key("wooden_shovel_head");
    public static final Key WOODEN_SICKLE_HEAD = Namespace.key("wooden_sickle_head");
    public static final Key WOODEN_SCYTHE_HEAD = Namespace.key("wooden_scythe_head");
    public static final Key WOODEN_BROAD_AXE_HEAD = Namespace.key("wooden_broad_axe_head");
    public static final Key WOODEN_MACE_HEAD = Namespace.key("wooden_mace_head");
    public static final Key WOODEN_HAMMER_HEAD = Namespace.key("wooden_hammer_head");
    public static final Key WOODEN_SWORD_BLADE = Namespace.key("wooden_sword_blade");
    public static final Key WOODEN_TOOL_HANDLE = Namespace.key("wooden_tool_handle");
    public static final Key WOODEN_TOOL_BINDING = Namespace.key("wooden_tool_binding");
    public static final Key WOODEN_LARGE_TOOL_BINDING = Namespace.key("wooden_large_tool_binding");
    public static final Key WOODEN_LARGE_TOOL_HANDLE = Namespace.key("wooden_large_tool_handle");
    public static final Key WOODEN_BOW_LIMB = Namespace.key("wooden_bow_limb");

    // POWER
    public static final Key CABLE = Namespace.key("cable");
    public static final Key SOLAR_PANEL = Namespace.key("solar_panel");
    public static final Key COBBLESTONE_GENERATOR = Namespace.key("cobblestone_generator");


    private static Key key(Material material) {
        return material.key();
    }

}
