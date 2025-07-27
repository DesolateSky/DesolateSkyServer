package net.desolatesky.block;

import net.desolatesky.util.Namespace;
import net.kyori.adventure.key.Key;
import net.minestom.server.tag.Tag;

public final class BlockTags {

    private BlockTags() {
        throw new UnsupportedOperationException();
    }

    public static final Tag<Key> ID = Tag.String("id").map(Namespace::key, Key::asString);
//    public static final Tag<Boolean> UNBREAKABLE = Tag.Boolean("unbreakable");
//    public static final Tag<Key> BLOCK_ITEM = Tag.String("block_item").map(Namespace::key, Key::asString);

}
