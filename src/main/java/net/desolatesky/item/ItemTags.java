package net.desolatesky.item;

import net.desolatesky.util.Pair;
import net.desolatesky.util.Tags;
import net.kyori.adventure.key.Key;
import net.minestom.server.tag.Tag;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public final class ItemTags {

    public static final Tag<Key> ID = Tags.Key("id");
    public static final Tag<Key> ISLAND_CORE_STORM_LOOT = Tags.Key("island_core_storm_loot");
    public static final Tag<Duration> ISLAND_CORE_STORM_DURATION = Tags.Duration("island_core_storm_duration");
    public static final Tag<Key> ISLAND_CORE_SPAWNER_KEY = Tags.Key("island_core_spawner_key");
    public static final Tag<Key> RECIPE_ID = Tags.Key("recipe_id");
    public static final Tag<Key> DROPPED_ITEM_ENTITY_KEY = Tags.Key("dropped_item_entity_key");
    public static final Tag<List<Pair<Key, Integer>>> CACHE_ITEMS_KEY = Tags.Pair("cached_items", Tags.Key("item_id"), Tag.Integer("amount")).list();

    public static final Tag<Double> PICKAXE_MINING_SPEED = Tags.Double("pickaxe_mining_speed");
    public static final Tag<Double> AXE_MINING_SPEED = Tags.Double("axe_mining_speed");
    public static final Tag<Double> SHOVEL_MINING_SPEED = Tags.Double("shovel_mining_speed");
    public static final Tag<Double> HOE_MINING_SPEED = Tags.Double("hoe_mining_speed");
    public static final Tag<Double> SWORD_MINING_SPEED = Tags.Double("sword_mining_speed");

    public static final Tag<Instant> ATTACK_COOLDOWN = Tags.Instant("attack-cooldown");

    public static final Tag<Double> COMPOSTER_VALUE = Tags.Double("composter_value");

    private ItemTags() {
        throw new UnsupportedOperationException();
    }
}
