package net.desolatesky.item.tool.registry;

import net.desolatesky.item.ItemTags;
import net.desolatesky.item.tool.part.ToolPart;
import net.desolatesky.item.tool.part.ToolParts;
import net.kyori.adventure.key.Key;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class ToolPartRegistry {

    public static ToolPartRegistry create() {
        return new ToolPartRegistry(new HashMap<>());
    }

    private final Map<Key, ToolPart> toolParts;

    private ToolPartRegistry(Map<Key, ToolPart> toolParts) {
        this.toolParts = toolParts;
    }

    public void load() {
        this.register(ToolParts.AXE_HEAD);
        this.register(ToolParts.HANDLE);
        this.register(ToolParts.BINDING);
    }

    private void register(ToolPart toolPart) {
        this.toolParts.put(toolPart.key(), toolPart);
    }

    public @Nullable ToolPart getPart(Key key) {
        return this.toolParts.get(key);
    }

    public @Nullable ToolPart getPartFromItem(ItemStack itemStack) {
        final Key key = itemStack.getTag(ItemTags.TOOL_PART);
        if (key == null) {
            return null;
        }
        return this.getPart(key);
    }

}
