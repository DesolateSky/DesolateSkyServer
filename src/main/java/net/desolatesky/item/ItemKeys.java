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

    // ITEMS
    public static final Key DUST = Namespace.key("dust");
    public static final Key COPPER_DUST = Namespace.key("copper_dust");
    public static final Key FIBER = Namespace.key("fiber");
    public static final Key FIBER_MESH = Namespace.key("fiber_mesh");

    // VANILLA
    public static final Key STICK = key(Material.STICK);
    public static final Key OAK_PLANKS = key(Material.OAK_PLANKS);
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
