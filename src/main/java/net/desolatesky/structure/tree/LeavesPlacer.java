package net.desolatesky.structure.tree;

import net.desolatesky.instance.DSInstance;
import net.desolatesky.structure.StructurePlaceResult;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;

import java.util.Map;

public interface LeavesPlacer {

    PlaceResult place(DSInstance instance, Point start, TrunkPlacer.PlaceResult trunkPlaceResult);

    record PlaceResult(boolean succeeded, Map<Point, Block> placed) implements StructurePlaceResult {

    }

}
