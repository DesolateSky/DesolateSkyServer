package net.desolatesky.item.tool.part;

import net.desolatesky.item.tool.action.BreakActionData;
import net.desolatesky.item.tool.action.ClickAirActionData;
import net.desolatesky.item.tool.action.ClickBlockActionData;
import net.desolatesky.item.tool.action.ClickEntityActionData;
import net.desolatesky.item.tool.action.ToolActionData;
import net.kyori.adventure.key.Keyed;
import net.minestom.server.item.ItemStack;

public interface ToolPart extends Keyed {

    default ToolActionData.Result modifyBreak(BreakActionData action, ItemStack toolPart) {
        return ToolActionData.Result.NOT_CONSUME_NOT_CANCEL;
    }

    default ToolActionData.Result modifyClick(ClickAirActionData action, ItemStack toolPart) {
        return ToolActionData.Result.NOT_CONSUME_NOT_CANCEL;
    }

    default ToolActionData.Result modifyClick(ClickBlockActionData action, ItemStack toolPart) {
        return ToolActionData.Result.NOT_CONSUME_NOT_CANCEL;
    }

    default ToolActionData.Result modifyClick(ClickEntityActionData action, ItemStack toolPart) {
        return ToolActionData.Result.NOT_CONSUME_NOT_CANCEL;
    }

    ToolPartType type();

}
