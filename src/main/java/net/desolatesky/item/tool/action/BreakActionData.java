package net.desolatesky.item.tool.action;

import net.desolatesky.instance.DSInstance;
import net.desolatesky.player.DSPlayer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;

import java.util.LinkedHashSet;
import java.util.SequencedSet;

public final class BreakActionData extends ToolActionData {

    private final Point blockPosition;
    private final Block blockBroken;
    private final SequencedSet<BlockToBreak> blockPositionsBroken;

    /**
     * @param toolUsed          the {@link ItemStack} used to actually break the block.
     * @param amountOfToolsUsed A tool can have multiple of the same part, for example 2 diamond pickaxe heads.
     *                          This is the amount of tools used, so if you have 2 diamond pickaxe heads, this will be 2.
     */
    public BreakActionData(DSPlayer player, DSInstance instance, Point blockPosition, Block blockBroken, ItemStack toolUsed, int amountOfToolsUsed) {
        super(player, instance, toolUsed, amountOfToolsUsed);
        this.blockPosition = blockPosition;
        this.blockBroken = blockBroken;
        this.blockPositionsBroken = new LinkedHashSet<>();
    }

    public Point blockPosition() {
        return this.blockPosition;
    }

    public Block blockBroken() {
        return this.blockBroken;
    }

    public SequencedSet<BlockToBreak> blockPositionsBroken() {
        return this.blockPositionsBroken;
    }

    public void addBlockToBreak(Point point, Block block) {
        this.blockPositionsBroken.add(new BlockToBreak(point, block));
    }

    public void removeBlockToBreak(Point point) {
        this.blockPositionsBroken.removeIf(blockToBreak -> blockToBreak.point().equals(point));
    }

    public record BlockToBreak(Point point, Block block) {
    }

}
