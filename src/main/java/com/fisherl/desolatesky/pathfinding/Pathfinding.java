package com.fisherl.desolatesky.pathfinding;

import de.bsommerfeld.pathetic.api.factory.PathfinderFactory;
import de.bsommerfeld.pathetic.api.pathing.NeighborStrategies;
import de.bsommerfeld.pathetic.api.pathing.Pathfinder;
import de.bsommerfeld.pathetic.api.pathing.configuration.PathfinderConfiguration;
import de.bsommerfeld.pathetic.api.wrapper.PathPosition;
import de.bsommerfeld.pathetic.engine.factory.AStarPathfinderFactory;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;

public final class Pathfinding {

    private Pathfinding() {}

    public static final PathfinderFactory FACTORY = new AStarPathfinderFactory();
    public static final Pathfinder PATHFINDER = FACTORY.createPathfinder(PathfinderConfiguration.builder()
            .provider((_, c) ->  {
                if (!(c instanceof final WorldPathfindingContext p)) {
                    return null;
                }
                return new PathPoint(p.world().asInstance(), true);
            })
            .neighborStrategy(NeighborStrategies.VERTICAL_AND_HORIZONTAL)
            .build());

    public static Point pointFromPathPosition(PathPosition pos) {
        return new Pos(pos.getX(), pos.getY(), pos.getZ());
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
