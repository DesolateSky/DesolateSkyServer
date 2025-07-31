package net.desolatesky.item.tool;

import com.google.common.base.Preconditions;
import net.desolatesky.util.Tags;
import net.kyori.adventure.key.Key;
import net.minestom.server.tag.Tag;
import net.minestom.server.tag.TagReadable;
import net.minestom.server.tag.TagSerializer;
import net.minestom.server.tag.TagWritable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public final class Tool {

    public static final TagSerializer<Tool> SERIALIZER = new Serializer();

    private final List<Key> toolParts;

    public Tool(List<Key> toolParts) {
        this.toolParts = toolParts;
    }

    public void replace(int index, Key toolPart) {
        Preconditions.checkElementIndex(index, this.toolParts.size());
        this.toolParts.set(index, toolPart);
    }

    public @Unmodifiable List<Key> toolParts() {
        return this.toolParts;
    }

    private static class Serializer implements TagSerializer<Tool> {

        private static final Tag<List<Key>> TOOL_PARTS_TAG = Tags.NamespaceKey("tool_parts").list();

        @Override
        public @Nullable Tool read(@NotNull TagReadable reader) {
            final List<Key> toolParts = reader.getTag(TOOL_PARTS_TAG);
            if (toolParts == null || toolParts.isEmpty()) {
                return null;
            }
            return new Tool(toolParts);
        }

        @Override
        public void write(@NotNull TagWritable writer, @NotNull Tool value) {
            writer.setTag(TOOL_PARTS_TAG, value.toolParts());
        }

    }



}
