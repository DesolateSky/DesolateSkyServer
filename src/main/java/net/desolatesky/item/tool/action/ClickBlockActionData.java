package net.desolatesky.item.tool.action;

import net.desolatesky.instance.DSInstance;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.PhysicalClickType;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;

public final class ClickBlockActionData extends ToolActionData {

    private final Block clickedBlock;
    private final Point blockPosition;
    private final PhysicalClickType clickType;

    public ClickBlockActionData(DSPlayer player, DSInstance instance, ItemStack toolUsed, int amountOfToolsUsed, Block clickedBlock, Point blockPosition, PhysicalClickType clickType) {
        super(player, instance, toolUsed, amountOfToolsUsed);
        this.clickedBlock = clickedBlock;
        this.blockPosition = blockPosition;
        this.clickType = clickType;
    }

    public Block clickedBlock() {
        return this.clickedBlock;
    }

    public Point blockPosition() {
        return this.blockPosition;
    }

    public PhysicalClickType clickType() {
        return this.clickType;
    }

}
