package com.fisherl.desolatesky.block.behavior;

import com.fisherl.desolatesky.util.BlockUtil;
import com.fisherl.desolatesky.world.DSWorld;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.Direction;

import java.util.function.Function;

public class DirectionalSpreadBehavior extends SpreadBehavior {

    private final Direction direction;
    private final int maxLength;

    public DirectionalSpreadBehavior(double spreadChance, Direction direction, int maxLength) {
        super(spreadChance);
        this.direction = direction;
        this.maxLength = maxLength;
    }

    @Override
    protected void spreadFrom(DSWorld world, Point pos, Block block, Key blockId) {
        final Point next = this.direction.vec().add(pos);
        if (!BlockUtil.isReplaceable(world.getBlock(next))) {
            return;
        }
        if (this.countLength(world, pos, block, blockId) >= this.maxLength) {
            return;
        }
        world.setBlock(next, blockId, Function.identity());
    }

    protected int countLength(DSWorld world, Point pos, Block block, Key blockId) {
        final Vec opposite = this.direction.opposite().vec();
        Point next = pos.add(opposite);
        int count = 1;
        while (blockId.equals(BlockUtil.getBlockId(world.getBlock(next)))) {
            next = next.add(opposite);
            count++;
        }
        return count;
    }

}
