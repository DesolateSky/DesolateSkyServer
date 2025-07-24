package net.desolatesky.entity;

import net.desolatesky.util.Namespace;
import net.kyori.adventure.key.Key;

public final class EntityKeys {

    private EntityKeys() {
        throw new UnsupportedOperationException();
    }

    public static final Key PLAYER_ENTITY = Namespace.key("player_entity");
    public static final Key DEBRIS_ENTITY = Namespace.key("debris_entity");
    public static final Key SIFTER_BLOCK_ENTITY = Namespace.key("sifter_block_entity");

}
