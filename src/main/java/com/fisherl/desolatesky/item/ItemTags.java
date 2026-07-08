package com.fisherl.desolatesky.item;

import com.fisherl.desolatesky.util.Tags;
import net.kyori.adventure.key.Key;
import net.minestom.server.tag.Tag;

import java.time.Duration;

public final class ItemTags {

    public static final Tag<Key> ID = Tags.Key("id");
    public static final Tag<Key> ISLAND_CORE_STORM_LOOT = Tags.Key("island_core_storm_loot");
    public static final Tag<Duration> ISLAND_CORE_STORM_DURATION = Tags.Duration("island_core_storm_duration");
    public static final Tag<Key> ISLAND_CORE_SPAWNER_KEY = Tags.Key("island_core_spawner_key");
    public static final Tag<Key> RECIPE_ID = Tags.Key("recipe_id");
    public static final Tag<Key> DROPPED_ITEM_ENTITY_KEY = Tags.Key("dropped_item_entity_key");

    private ItemTags() {
        throw new UnsupportedOperationException();
    }
}
