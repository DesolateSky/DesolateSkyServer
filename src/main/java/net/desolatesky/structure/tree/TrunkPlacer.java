package net.desolatesky.structure.tree;

import net.desolatesky.instance.DSInstance;
import net.desolatesky.structure.StructurePlaceResult;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;

public interface TrunkPlacer {

    PlaceResult place(DSInstance instance, Point origin);

        record PlaceResult(boolean succeeded, @Unmodifiable Map<Point, Block> placed, int height, int radius) implements StructurePlaceResult {

        }

}
