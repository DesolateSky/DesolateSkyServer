package net.desolatesky.block;

import net.desolatesky.util.Namespace;
import net.kyori.adventure.key.Key;
import net.minestom.server.instance.block.Block;

public final class BlockKeys {

    private BlockKeys() {
        throw new UnsupportedOperationException();
    }

    public static final Key DEBRIS_CATCHER = Namespace.key("debris_catcher");
    public static final Key DUST_BLOCK = Namespace.key("dust_block");
    public static final Key COMPOSTER = Namespace.key("composter");
    public static final Key SIFTER = Namespace.key("sifter");


    public static final Key CRAFTING_TABLE = key(Block.CRAFTING_TABLE);
    public static final Key WAXED_EXPOSED_COPPER_TRAPDOOR = key(Block.WAXED_EXPOSED_COPPER_TRAPDOOR);
    public static final Key DIRT = key(Block.DIRT);
    public static final  Key SAND = key(Block.SAND);
    public static final Key GRAVEL = key(Block.GRAVEL);
    public static final Key CLAY = Namespace.key("clay");
    public static final Key WHEAT = key(Block.WHEAT);

    // PETRIFIED WOOD
    public static final Key PETRIFIED_SAPLING = Namespace.key("petrified_sapling");
    public static final Key PETRIFIED_LOG = Namespace.key("petrified_log");
    public static final Key STRIPPED_PETRIFIED_LOG = Namespace.key("stripped_petrified_log");
    public static final Key PETRIFIED_LEAVES = Namespace.key("petrified_leaves");
    public static final Key PETRIFIED_PLANKS = Namespace.key("petrified_planks");
    public static final Key PETRIFIED_SLAB = Namespace.key("petrified_slab");


    private static Key key(Block block) {
        return block.key();
    }

}
