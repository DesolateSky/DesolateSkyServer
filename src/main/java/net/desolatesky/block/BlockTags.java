package net.desolatesky.block;

import net.desolatesky.util.Namespace;
import net.desolatesky.util.Tags;
import net.kyori.adventure.key.Key;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.Direction;

import java.time.Instant;
import java.util.UUID;

public final class BlockTags {

    private BlockTags() {}

    public static final Tag<Key> ID = Tags.Key(Namespace.key("id").asString());
    public static final Tag<Direction> FACING = Tags.Enum("direction", Direction.class);

    // Island Core
    public static final Tag<Boolean> ISLAND_CORE_STORM_ACTIVE = Tags.Boolean("powered");
    public static final Tag<Key> ISLAND_CORE_STORM_LOOT_TABLE = Tags.Key("storm_loot_table");
    public static final Tag<Instant> ISLAND_CORE_STORM_END = Tags.Instant("storm_end");
    public static final Tag<UUID> ISLAND_CORE_DISPLAY_ENTITY_ID = Tags.UUID("display_entity");
    public static final Tag<Key> ISLAND_CORE_SPAWNER_KEY = Tags.Key("spawner");

    public static final Tag<Double> CACTUS_FLOWER_WATER = Tags.Double("cactus_flower_water_buckets");

    public static final Tag<Boolean> IS_VOID_CROP = Tags.Boolean("is_void_crop");
    public static final Tag<UUID> VOID_CROP_ENTITY = Tags.UUID("void_crop_entity");

}
