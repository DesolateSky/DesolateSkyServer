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
    public static final Key SIFTER = Namespace.key("sifter");

    public static final Key CRAFTING_TABLE = key(Block.CRAFTING_TABLE);
    public static final Key OAK_PLANKS = key(Block.OAK_PLANKS);

    private static Key key(Block block) {
        return block.key();
    }

}
