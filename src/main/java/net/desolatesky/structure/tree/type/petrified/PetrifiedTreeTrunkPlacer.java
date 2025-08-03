package net.desolatesky.structure.tree.type.petrified;

import net.desolatesky.block.DSBlocks;
import net.desolatesky.block.entity.BlockEntities;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.structure.tree.TrunkPlacer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.random.RandomGenerator;

public final class PetrifiedTreeTrunkPlacer implements TrunkPlacer {

    private final int minRadius;
    private final int maxRadius;
    private final int minHeight;
    private final int maxHeight;
    private final BlockEntities blockEntities;

    public PetrifiedTreeTrunkPlacer(int minRadius, int maxRadius, int minHeight, int maxHeight, BlockEntities blockEntities) {
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.blockEntities = blockEntities;
    }

    @Override
    public PlaceResult place(DSInstance instance, Point origin) {
        final RandomGenerator random = instance.randomSource();
        final int radius = random.nextInt(this.minRadius, this.maxRadius + 1);
        final int height = random.nextInt(this.minHeight, this.maxHeight + 1);
        final Map<Point, Block> blocks = new HashMap<>();
        int highestPlace = origin.blockY();
        for (int y = 0; y < height; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (Math.abs(x) + Math.abs(z) <= radius) {
                        final Point blockPosition = origin.add(x, y, z);
                        if (instance.getBlock(blockPosition).isAir()) {
                            blocks.put(blockPosition, DSBlocks.PETRIFIED_LOG.create(this.blockEntities));
                            highestPlace = Math.max(highestPlace, blockPosition.blockY());
                            continue;
                        }
                        return new PlaceResult(false, Collections.emptyMap(), 0, 0);
                    }
                }
            }
        }
        return new PlaceResult(
                true,
                blocks,
                height,
                radius
        );
    }

}
