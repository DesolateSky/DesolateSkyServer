package net.desolatesky.item.tool;

import com.google.common.base.Preconditions;
import net.desolatesky.item.DSItem;
import net.desolatesky.item.DSItemRegistry;
import net.desolatesky.item.ItemTags;
import net.desolatesky.item.tool.part.ToolPart;
import net.desolatesky.item.tool.part.ToolPartType;
import net.desolatesky.item.tool.registry.ToolPartRegistry;
import net.desolatesky.tag.Tags;
import net.desolatesky.util.collection.Pair;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minestom.server.component.DataComponents;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import net.minestom.server.tag.TagReadable;
import net.minestom.server.tag.TagSerializer;
import net.minestom.server.tag.TagWritable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class Tool {

    public static final TagSerializer<Tool> SERIALIZER = new Serializer();

    private final Key itemKey;
    private final List<Key> toolPartItemKeys;

    public Tool(Key itemKey, List<Key> toolPartItemKeys) {
        this.itemKey = itemKey;
        this.toolPartItemKeys = toolPartItemKeys;
    }

    public void replace(int index, Key toolPart) {
        Preconditions.checkElementIndex(index, this.toolPartItemKeys.size());
        this.toolPartItemKeys.set(index, toolPart);
    }

    public ToolPart getHeadPart(DSItemRegistry itemRegistry, ToolPartRegistry toolPartRegistry) {
        for (final Key itemPartKey : this.toolPartItemKeys) {
            final DSItem partItem = itemRegistry.getItem(itemPartKey);
            if (partItem == null) {
                throw new IllegalArgumentException("Item with key " + itemPartKey + " not found in tool: " + this.itemKey);
            }
            final Key partKey = partItem.getTag(ItemTags.TOOL_PART);
            if (partKey == null) {
                throw new IllegalArgumentException("Item with key " + itemPartKey + " is not a tool part in tool: " + this.itemKey);
            }
            final ToolPart part = toolPartRegistry.getPart(partKey);
            if (part != null && part.type().isHead()) {
                return part;
            }
        }
        throw new IllegalStateException("No head part found in tool: " + this.itemKey);
    }

    public ItemStack getHeadPartItem(DSItemRegistry itemRegistry) {
        for (final Key itemPartKey : this.toolPartItemKeys) {
            final DSItem partItem = itemRegistry.getItem(itemPartKey);
            if (partItem == null) {
                throw new IllegalArgumentException("Item with key " + itemPartKey + " not found in tool: " + this.itemKey);
            }
            return partItem.create();
        }
        throw new IllegalStateException("No head part item found in tool: " + this.itemKey);
    }

    public ItemStack createItemStack(DSItemRegistry itemRegistry, ToolPartRegistry toolPartRegistry) {
        final DSItem item = itemRegistry.getItem(this.itemKey);
        if (item == null) {
            throw new IllegalArgumentException("Item with key " + this.itemKey + " not found");
        }
        final List<Pair<ItemStack, ToolPart>> toolPartItems = this.toolPartItemKeys.stream()
                .map(partItemkey -> {
                    final DSItem partItem = Objects.requireNonNull(itemRegistry.getItem(partItemkey), "Tool part item not found: " + partItemkey);
                    return partItem.create();
                })
                .map(partItem -> {
                    final ToolPart toolPart = Objects.requireNonNull(toolPartRegistry.getPartFromItem(partItem), "Tool part not found for item: " + partItem);
                    return Pair.of(partItem, toolPart);
                })
                .sorted(Comparator.comparingInt(itemStackToolPartPair -> itemStackToolPartPair.second().type().order()))
                .toList();
        System.out.println("Tool part items: " + toolPartItems);
        final List<Component> description = new ArrayList<>();
        Key toolModelKey = null;
        for (final Pair<ItemStack, ToolPart> toolPair : toolPartItems) {
            final ItemStack toolPartItem = toolPair.first();
            final ToolPart toolPart = toolPair.second();
            System.out.println("Tool part item: " + toolPartItem.get(DataComponents.CUSTOM_NAME));
            description.add(toolPart.getDisplayName(toolPartItem));
            description.addAll(toolPart.getDescription(toolPartItem));
            final ToolPartType type = toolPart.type();
            if (toolModelKey == null && type.isHead()) {
                toolModelKey = toolPartItem.getTag(ItemTags.TOOL_MODEL);
            }
        }
        if (toolModelKey == null) {
            throw new IllegalStateException("Tool model key not found for tool: " + this.itemKey);
        }
        System.out.println("Tool model key: " + toolModelKey);
        return item.create()
                .withTag(ItemTags.ITEM_ID, this.itemKey)
                .withTag(ItemTags.TOOL, this)
                .withLore(description)
                .with(DataComponents.ITEM_MODEL, toolModelKey.asString());
    }

    public Key itemKey() {
        return this.itemKey;
    }

    public @Unmodifiable List<Key> toolPartItemKeys() {
        return this.toolPartItemKeys;
    }

    private static class Serializer implements TagSerializer<Tool> {

        private static final Tag<List<Key>> TOOL_PARTS_TAG = Tags.NamespaceKey("tool_parts").list();

        @Override
        public @Nullable Tool read(@NotNull TagReadable reader) {
            final Key itemKey = reader.getTag(ItemTags.ITEM_ID);
            if (itemKey == null) {
                return null;
            }
            final List<Key> toolParts = reader.getTag(TOOL_PARTS_TAG);
            if (toolParts == null || toolParts.isEmpty()) {
                return null;
            }
            return new Tool(itemKey, toolParts);
        }

        @Override
        public void write(@NotNull TagWritable writer, @NotNull Tool value) {
            writer.setTag(ItemTags.ITEM_ID, value.itemKey());
            writer.setTag(TOOL_PARTS_TAG, value.toolPartItemKeys());
        }

    }


}
