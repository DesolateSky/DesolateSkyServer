package net.desolatesky.entity.ai.goal;

import de.bsommerfeld.pathetic.engine.result.PathUtils;
import net.desolatesky.entity.DSLivingEntity;
import net.desolatesky.island.Island;
import net.desolatesky.pathfinding.Pathfinding;
import net.desolatesky.pathfinding.WorldPathfindingContext;
import net.desolatesky.world.DSWorld;
import de.bsommerfeld.pathetic.api.wrapper.PathPosition;
import net.desolatesky.world.VoidWorld;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.attribute.Attribute;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

public final class PathGoal<T extends DSLivingEntity<T>> implements AIGoal {

    private final T entity;
    private final AtomicBoolean findingPath = new AtomicBoolean(false);
    private final AtomicBoolean pathingInterrupted = new AtomicBoolean(false);
    private @Nullable Iterator<PathPosition> path;
    private @Nullable Point currentTarget;

    public PathGoal(T entity) {
        this.entity = entity;
    }

    @Override
    public boolean canStart() {
        return this.path == null;
    }

    @Override
    public void start() {
        if (this.findingPath.getAndSet(true) && !this.pathingInterrupted.get()) {
            return;
        }
        this.pathingInterrupted.set(false);
        this.findingPath.set(true);
        final Point pos = this.entity.getPosition();
        final PathPosition start = new PathPosition(pos.x(), pos.y(), pos.z());
        final Point islandCorePos = VoidWorld.SPAWN_POINT.sub(0, 1, 0);
        final PathPosition target = new PathPosition(islandCorePos.blockX(), islandCorePos.blockY(), islandCorePos.blockZ());

        if (!(this.entity.getInstance() instanceof final DSWorld world)) {
            return;
        }
        Pathfinding.PATHFINDER.findPath(start, target, new WorldPathfindingContext(world)).ifPresent(result -> {
            // Do movement logic here
            this.entity.acquirable().sync(_ -> this.path = PathUtils.mutatePositions(result.getPath(), p -> p.add(0.5, 0, 0.5)).iterator());
        }).orElse(result -> System.err.println("Path failed. Reason: " + result.getPathState()));
    }

    @Override
    public void tick(long time) {
        if (this.path == null) {
            return;
        }
        if (this.entity.ticksSinceLastDamage() < 20 && this.entity.ticksSinceLastDamage() >= 0) {
            this.path = null;
            this.currentTarget = null;
            this.pathingInterrupted.set(true);
            return;
        }
        if (this.pathingInterrupted.get()) {
            this.start();
            return;
        }
        if (this.path.hasNext() && this.currentTarget == null || (this.path.hasNext() && this.entity.getDistanceSquared(this.currentTarget) <= 0.5)) {
            this.currentTarget = Pathfinding.pointFromPathPosition(this.path.next());
        }
        if (this.currentTarget == null || (this.entity.getDistanceSquared(this.currentTarget) <=  0.5 && !this.path.hasNext())) {
            this.findingPath.set(false);
            return;
        }
        final Vec move = this.currentTarget.sub(this.entity.getPosition()).asVec().normalize().mul(this.entity.getAttributeValue(Attribute.MOVEMENT_SPEED));
        this.entity.lookAt(this.currentTarget);
        final Vec currentVelocity = this.entity.getVelocity();
        this.entity.setVelocity(currentVelocity.add(move.add(0, 1, 0)));
    }

    @Override
    public boolean shouldEnd() {
        return !this.findingPath.get();
    }

    @Override
    public void end() {
        this.path = null;
        this.currentTarget = null;
        this.findingPath.set(false);
        this.entity.remove();
    }
}
