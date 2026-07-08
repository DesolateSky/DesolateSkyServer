package com.fisherl.desolatesky.entity;

import com.fisherl.desolatesky.util.Tags;
import net.kyori.adventure.key.Key;
import net.minestom.server.tag.Tag;

public final class EntityTags {

    private EntityTags() {}

    public static final Tag<Key> ITEM_DISPLAY_KEY = Tags.Key("block_display");
    public static final Tag<Key> ITEM = Tags.Key("item");
    public static final Tag<Boolean> ISLAND_CORE_MOB = Tags.Boolean("island_core_mob");

}
