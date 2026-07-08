package com.fisherl.desolatesky.pathfinding;

import com.fisherl.desolatesky.world.DSWorld;
import de.bsommerfeld.pathetic.api.pathing.context.EnvironmentContext;

public class WorldPathfindingContext implements EnvironmentContext {

    private final DSWorld world;

    public WorldPathfindingContext(DSWorld world) {
        this.world = world;
    }

    public DSWorld world() {
        return this.world;
    }
}
