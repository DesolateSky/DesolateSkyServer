package net.desolatesky.item.tool.part.type;

import net.desolatesky.item.tool.part.ToolPart;
import net.desolatesky.item.tool.part.ToolPartType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record NoActionToolPart(@NotNull Key key, ToolPartType type) implements ToolPart {

    @Override
    public List<Component> getDescription(ItemStack toolPartItem) {
        return List.of();
    }

}
