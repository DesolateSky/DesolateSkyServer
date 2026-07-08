package com.fisherl.desolatesky.pathfinding;

import de.bsommerfeld.pathetic.api.provider.NavigationPoint;
import net.minestom.server.instance.Instance;

public final class PathPoint implements NavigationPoint {

    private final Instance instance;
    private final boolean traversable;

    public PathPoint(Instance instance, boolean traversable) {
        this.instance = instance;
        this.traversable = traversable;
    }

    @Override
    public boolean isTraversable() {
        return this.traversable;
    }
}
