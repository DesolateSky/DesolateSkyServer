package net.desolatesky.item.tool.part;

import net.desolatesky.item.handler.ItemInteractionResult;
import net.desolatesky.item.tool.action.BreakBlockActionData;
import net.desolatesky.item.tool.action.ClickAirActionData;
import net.desolatesky.item.tool.action.ClickBlockActionData;
import net.desolatesky.item.tool.action.ClickEntityActionData;
import net.kyori.adventure.key.Keyed;
import net.kyori.adventure.text.Component;
import net.minestom.server.component.DataComponents;
import net.minestom.server.item.ItemStack;

import java.util.List;

public interface ToolPart extends Keyed {

    default ItemInteractionResult modifyBreak(BreakBlockActionData action, ItemStack toolPart) {
        return ItemInteractionResult.noEffect();
    }

    default ItemInteractionResult modifyClick(ClickAirActionData action, ItemStack toolPart) {
        return ItemInteractionResult.noEffect();
    }

    default ItemInteractionResult modifyClick(ClickBlockActionData action, ItemStack toolPart) {
        return ItemInteractionResult.noEffect();
    }

    default ItemInteractionResult modifyClick(ClickEntityActionData action, ItemStack toolPart) {
        return ItemInteractionResult.noEffect();
    }

    default Component getDisplayName(ItemStack toolPartItem) {
        return toolPartItem.get(DataComponents.CUSTOM_NAME);
    }

    List<Component> getDescription(ItemStack toolPartItem);

    ToolPartType type();

}
