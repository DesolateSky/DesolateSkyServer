package net.desolatesky.item;

import net.desolatesky.util.Namespace;
import net.kyori.adventure.key.Key;
import net.minestom.server.tag.Tag;

public final class ItemTags {

    private ItemTags() {
        throw new UnsupportedOperationException();
    }

    public static final Tag<Key> ID = Tag.String("id").map(Namespace::key, Key::asString);

}
