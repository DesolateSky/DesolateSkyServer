package net.desolatesky.structure.tree;

import net.desolatesky.instance.DSInstance;
import net.desolatesky.structure.Structure;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;

import java.util.Map;

public class TreeStructure implements Structure {

    private final TrunkPlacer trunkPlacer;
    private final LeavesPlacer leavesPlacer;

    public static TreeStructure createTreeStructure(TrunkPlacer trunkPlacer, LeavesPlacer leavesPlacer) {
        return new TreeStructure(trunkPlacer, leavesPlacer);
    }

    public TreeStructure(TrunkPlacer trunkPlacer, LeavesPlacer leavesPlacer) {
        this.trunkPlacer = trunkPlacer;
        this.leavesPlacer = leavesPlacer;
    }

    @Override
    public void place(DSInstance instance, Point origin) {
        final TrunkPlacer.PlaceResult trunkResult = this.trunkPlacer.place(instance, origin);
        if (!trunkResult.succeeded()) {
            return;
        }
        final LeavesPlacer.PlaceResult leavesResult = this.leavesPlacer.place(instance, origin, trunkResult);
        if (!leavesResult.succeeded()) {
            return;
        }
        final Map<Point, Block> trunkBlocks = trunkResult.placed();
        final Map<Point, Block> leavesBlocks = leavesResult.placed();
        for (final Map.Entry<Point, Block> entry : trunkBlocks.entrySet()) {
            final Point blockPosition = entry.getKey();
            final Block block = entry.getValue();
            instance.setBlock(blockPosition, block);
        }
        for (final Map.Entry<Point, Block> entry : leavesBlocks.entrySet()) {
            final Point blockPosition = entry.getKey();
            final Block block = entry.getValue();
            instance.setBlock(blockPosition, block);
        }
    }

}
