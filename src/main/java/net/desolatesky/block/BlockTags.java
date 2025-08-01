package net.desolatesky.block;

import net.desolatesky.block.entity.custom.crop.Crop;
import net.desolatesky.util.Tags;
import net.kyori.adventure.key.Key;
import net.minestom.server.tag.Tag;

public final class BlockTags {

    private BlockTags() {
        throw new UnsupportedOperationException();
    }

    public static final Tag<Key> ID = Tags.NamespaceKey("block_id");
    public static final Tag<Double> COMPOSTER_LEVEL = Tags.Double("composter_level");
    public static final Tag<Key> STRIPS_TO = Tags.NamespaceKey("strips_to");
    public static final Tag<Integer> CROP_AGE = Tags.Integer("crop_age");
    public static final Tag<Double> CROP_GROWTH_CHANCE = Tags.Double("crop_growth_chance");
    public static final Tag<Crop> CROP = Tag.Structure("crop", Crop.SERIALIZER);

}
