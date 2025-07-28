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
    public static final Key PETRIFIED_PLANKS = key(Block.PALE_OAK_PLANKS);
    public static final Key PETRIFIED_SLAB = key(Block.PALE_OAK_SLAB);

    public static final Key CRAFTING_TABLE = key(Block.CRAFTING_TABLE);
    public static final Key WAXED_EXPOSED_COPPER_TRAPDOOR = key(Block.WAXED_EXPOSED_COPPER_TRAPDOOR);
    public static final Key DIRT = key(Block.DIRT);


    private static Key key(Block block) {
        return block.key();
    }

}
