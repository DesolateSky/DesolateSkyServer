package net.desolatesky.item.tool.action;

import net.desolatesky.instance.DSInstance;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.PhysicalClickType;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.item.ItemStack;

public final class ClickAirActionData extends ToolActionData {

    private final PhysicalClickType clickType;

    public ClickAirActionData(DSPlayer player, DSInstance instance, ItemStack toolUsed, PlayerHand hand, PhysicalClickType clickType) {
        super(player, instance, toolUsed, hand);
        this.clickType = clickType;
    }

    public PhysicalClickType clickType() {
        return this.clickType;
    }

}
