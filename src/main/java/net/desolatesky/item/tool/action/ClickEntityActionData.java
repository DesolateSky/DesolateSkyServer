package net.desolatesky.item.tool.action;

import net.desolatesky.entity.DSEntity;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.PhysicalClickType;
import net.minestom.server.item.ItemStack;

public final class ClickEntityActionData extends ToolActionData {

    private final DSEntity clickedEntity;
    private final PhysicalClickType clickType;

    public ClickEntityActionData(DSPlayer player, DSInstance instance, ItemStack toolUsed, int amountOfToolsUsed, DSEntity clickedEntity, PhysicalClickType clickType) {
        super(player, instance, toolUsed, amountOfToolsUsed);
        this.clickedEntity = clickedEntity;
        this.clickType = clickType;
    }

    public DSEntity clickedEntity() {
        return this.clickedEntity;
    }

    public PhysicalClickType clickType() {
        return this.clickType;
    }

}
