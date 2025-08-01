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
    public static final Key COMPOSTER = Namespace.key("composter");
    public static final Key DIRT = Namespace.key("dirt");
    public static final Key PETRIFIED_PLANKS = Namespace.key("petrified_planks");
    public static final Key PETRIFIED_SLAB = Namespace.key("petrified_slab");

    // ITEMS
    public static final Key DUST = Namespace.key("dust");
    public static final Key COPPER_DUST = Namespace.key("copper_dust");
    public static final Key FIBER = Namespace.key("fiber");
    public static final Key FIBER_MESH = Namespace.key("fiber_mesh");
    public static final Key DEAD_LEAVES = Namespace.key("dead_leaves");
    public static final Key PETRIFIED_STICK = Namespace.key("petrified_stick");
    public static final Key WHEAT_SEEDS = key(Material.WHEAT_SEEDS);

    // VANILLA
    public static final Key CRAFTING_TABLE = key(Material.CRAFTING_TABLE);
    public static final Key STRING = key(Material.STRING);

    public static final Key WAXED_EXPOSED_COPPER_TRAPDOOR = key(Material.WAXED_EXPOSED_COPPER_TRAPDOOR);
    public static final Key WAXED_EXPOSED_CUT_COPPER_SLAB = key(Material.WAXED_EXPOSED_CUT_COPPER_SLAB);

    //  TOOLS
    public static final Key WOODEN_AXE = key(Material.WOODEN_AXE);

    private static Key key(Material material) {
        return material.key();
    }

}
