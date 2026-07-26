package net.desolatesky.pathfinding;

import de.bsommerfeld.pathetic.api.factory.PathfinderFactory;
import de.bsommerfeld.pathetic.api.pathing.NeighborStrategies;
import de.bsommerfeld.pathetic.api.pathing.Pathfinder;
import de.bsommerfeld.pathetic.api.pathing.configuration.PathfinderConfiguration;
import de.bsommerfeld.pathetic.api.wrapper.PathPosition;
import de.bsommerfeld.pathetic.engine.factory.AStarPathfinderFactory;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

import java.util.List;

public final class Pathfinding {

    private Pathfinding() {
    }

    public static final PathfinderFactory FACTORY = new AStarPathfinderFactory();
    public static final Pathfinder PATHFINDER = FACTORY.createPathfinder(PathfinderConfiguration.builder()
            .provider((pos, c) -> {
                if (!(c instanceof final WorldPathfindingContext p)) {
                    return null;
                }
                final Point point = pointFromPathPosition(pos);
                final Instance world = p.world();
                final boolean traversable = isTraversable(world, point);
                return new PathPoint(world, traversable);
            })
            .validationProcessors(List.of(context -> {
                if (!(context.getEnvironmentContext() instanceof final WorldPathfindingContext c)) {
                    return false;
                }
                return isTraversable(c.world(), pointFromPathPosition(context.getCurrentPathPosition()));
            }))
            .neighborStrategy(NeighborStrategies.DIAGONAL_3D)
            .build());

    public static Point pointFromPathPosition(PathPosition pos) {
        return new Pos(pos.getX(), pos.getY(), pos.getZ());
    }

    public static PathPosition pathPositionFromPoint(Point point) {
        return new PathPosition(point.x(), point.y(), point.z());
    }

    public static boolean isTraversable(Instance world, Point point) {
        final Point under = point.sub(0, 1, 0);
        final Block at = world.getBlock(point);
        return world.getBlock(under).isSolid() &&
                (!at.registry().blocksMotion() ||
                !at.registry().collisionShape()
                        .intersectBox(point, BoundingBox.fromPoints(point, point)));
    }
//
//    // 4. Define where you are and where you want to go
//    final PathPosition start = new PathPosition(1, 2, 3);
//    final PathPosition target = new PathPosition(10, 5, 20);
//
//    // 5. Fire and forget (Async)
//    // Note: In a real scenario, pass your EnvironmentContext here.
//        pathfinder.findPath(start, target).ifPresent(result -> {
//        System.out.println("Easy. Path length: " + result.getPath().collect());
//        // Do movement logic here
//        this.entityCreature.acquirable().sync(entity -> this.pathIterator = result.getPath().iterator());
//        this.pathIterator = result.getPath().iterator();
//    }).orElse(result -> System.err.println("Path failed. Reason: " + result.getPathState()));

}
