package net.desolatesky.item;

import net.desolatesky.util.Namespace;
import net.kyori.adventure.key.Key;

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
    public static final Key STICK = Namespace.minecraftKey("key");
    public static final Key OAK_PLANKS = Namespace.minecraftKey("oak_planks");
    public static final Key CRAFTING_TABLE = Namespace.minecraftKey("crafting_table");
    public static final Key STRING = Namespace.minecraftKey("string");

}
