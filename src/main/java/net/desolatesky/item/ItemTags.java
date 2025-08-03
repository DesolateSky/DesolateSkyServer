package net.desolatesky.item;

import net.desolatesky.block.entity.custom.crop.Crop;
import net.desolatesky.item.tool.Tool;
import net.desolatesky.item.tool.ToolMaterial;
import net.desolatesky.tag.Tags;
import net.kyori.adventure.key.Key;
import net.minestom.server.tag.Tag;

public final class ItemTags {

    private ItemTags() {
        throw new UnsupportedOperationException();
    }

    public static final Tag<Key> ITEM_ID = Tags.NamespaceKey("item_id");
    public static final Tag<Key> BLOCK_ID = Tags.NamespaceKey("block_id");
    public static final Tag<Double> COMPOSTER_VALUE = Tags.Double("composter_value");
    public static final Tag<Tool> TOOL = Tags.Structure("tool", Tool.SERIALIZER);
    public static final Tag<Key> TOOL_PART = Tags.NamespaceKey("tool_part");
    public static final Tag<ToolMaterial> TOOL_MATERIAL = Tags.Enum("tool_material", ToolMaterial.class);
    public static final Tag<Key> TOOL_MODEL = Tags.NamespaceKey("tool_model"); // stored on the head tool part to be used to set on the final item
    public static final Tag<Crop> CROP = Tag.Structure("crop", Crop.SERIALIZER);

}
