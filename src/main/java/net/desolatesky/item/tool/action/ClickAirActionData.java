package net.desolatesky.item.tool.action;

import net.desolatesky.instance.DSInstance;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.PhysicalClickType;
import net.minestom.server.item.ItemStack;

public final class ClickAirActionData extends ToolActionData {

    private final PhysicalClickType clickType;

    public ClickAirActionData(DSPlayer player, DSInstance instance, ItemStack toolUsed, int amountOfToolsUsed, PhysicalClickType clickType) {
        super(player, instance, toolUsed, amountOfToolsUsed);
        this.clickType = clickType;
    }

    public PhysicalClickType clickType() {
        return this.clickType;
    }

}
