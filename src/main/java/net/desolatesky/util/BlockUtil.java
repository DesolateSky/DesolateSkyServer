package net.desolatesky.util;

import net.desolatesky.block.BlockTags;
import net.kyori.adventure.key.Key;
import net.minestom.server.instance.block.Block;

public final class BlockUtil {

    private BlockUtil() {}

    public static Key getBlockId(Block block) {
        final Key key = block.getTag(BlockTags.ID);
        if (key == null) {
            return block.key();
        }
        return key;
    }

    public static boolean isReplaceable(Block block) {
        return block.replaceable();
    }

}
