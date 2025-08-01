package net.desolatesky.item;

import net.desolatesky.block.entity.custom.crop.Crop;
import net.desolatesky.item.tool.Tool;
import net.desolatesky.util.Tags;
import net.kyori.adventure.key.Key;
import net.minestom.server.tag.Tag;

import java.util.List;

public final class ItemTags {

    private ItemTags() {
        throw new UnsupportedOperationException();
    }

    public static final Tag<Key> ID = Tags.NamespaceKey("id");
    public static final Tag<Key> BLOCK_ID = Tags.NamespaceKey("block_id");
    public static final Tag<Double> COMPOSTER_VALUE = Tags.Double("composter_value");
    public static final Tag<Tool> TOOL = Tags.Structure("tool", Tool.SERIALIZER);
    public static final Tag<Crop> CROP = Tag.Structure("crop", Crop.SERIALIZER);

}
