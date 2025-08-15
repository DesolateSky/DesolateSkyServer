package net.desolatesky.block;

import net.desolatesky.block.entity.custom.crop.Crop;
import net.desolatesky.block.entity.custom.powered.cable.CableSettings;
import net.desolatesky.tag.Tags;
import net.kyori.adventure.key.Key;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.Direction;

import java.util.List;

public final class BlockTags {

    private BlockTags() {
        throw new UnsupportedOperationException();
    }

    public static final Tag<Key> ID = Tags.NamespaceKey("block_id");
    public static final Tag<Double> COMPOSTER_LEVEL = Tags.Double("composter_level");
    public static final Tag<Key> STRIPS_TO = Tags.NamespaceKey("strips_to");
    public static final Tag<Integer> CROP_AGE = Tags.Integer("crop_age");
    public static final Tag<Double> CROP_GROWTH_CHANCE = Tags.Double("crop_growth_chance");
    public static final Tag<Crop> CROP = Tags.Structure("crop", Crop.SERIALIZER);
    public static final  Tag<Direction> DIRECTION_TAG = Tags.Enum("direction", Direction.class);
    public static final Tag<List<Direction>> CONNECTIONS_TAG = Tags.Enum("connections", Direction.class).list();


    // TRANSIENT TAGS FOR BLOCK SETTINGS
    public static final Tag<CableSettings> CABLE_SETTINGS = Tags.Transient("cable_settings");

}
