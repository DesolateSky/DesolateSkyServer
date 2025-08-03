package net.desolatesky.structure.tree.type.petrified;

import net.desolatesky.block.DSBlocks;
import net.desolatesky.block.entity.BlockEntities;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.structure.tree.LeavesPlacer;
import net.desolatesky.structure.tree.TrunkPlacer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.random.RandomGenerator;

public final class PetrifiedTreeLeavesPlacer implements LeavesPlacer {

    private final int minRadius;
    private final int maxRadius;
    private final int minHeight;
    private final int maxHeight;
    private final BlockEntities blockEntities;

    public PetrifiedTreeLeavesPlacer(int minRadius, int maxRadius, int minHeight, int maxHeight, BlockEntities blockEntities) {
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.blockEntities = blockEntities;
    }

    @Override
    public PlaceResult place(DSInstance instance, Point start, TrunkPlacer.PlaceResult trunkPlaceResult) {
        final Map<Point, Block> leaves = new HashMap<>();
        final RandomGenerator random = instance.randomSource();
        final int radius = random.nextInt(this.minRadius, this.maxRadius + 1);
        final int height = random.nextInt(this.minHeight, this.maxHeight + 1);
        for (int y = 0; y < height; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    // want smaller radius if higher up
                    final int adjustedRadius = (int) Math.ceil(radius * (1.0 - (double) y / height));
                    if (Math.abs(x) + Math.abs(z) <= adjustedRadius) {
                        final Point leafPosition = start.add(x, y + trunkPlaceResult.height() - 1, z);
                        final Block currentBlock = instance.getBlock(leafPosition);
                        if (!currentBlock.isAir()) {
                            return new PlaceResult(false, Collections.emptyMap());
                        }
                        final Block log = trunkPlaceResult.placed().get(leafPosition);
                        if (log != null) {
                            continue;
                        }
                        final Block leafBlock = DSBlocks.PETRIFIED_LEAVES.create(this.blockEntities);
                        leaves.put(leafPosition, leafBlock);
                    }
                }
            }
        }
        return new PlaceResult(true, leaves);
    }

}
