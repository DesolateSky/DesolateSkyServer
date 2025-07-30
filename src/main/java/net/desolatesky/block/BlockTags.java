package net.desolatesky.block;

import net.desolatesky.util.Tags;
import net.kyori.adventure.key.Key;
import net.minestom.server.tag.Tag;

public final class BlockTags {

    private BlockTags() {
        throw new UnsupportedOperationException();
    }

    public static final Tag<Key> ID = Tags.NamespaceKey("block_id");
    public static final Tag<Double> COMPOSTER_LEVEL = Tags.Double("composter_level");

}
