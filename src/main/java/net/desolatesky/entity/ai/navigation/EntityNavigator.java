package net.desolatesky.entity.ai.navigation;

import de.bsommerfeld.pathetic.api.wrapper.PathPosition;
import de.bsommerfeld.pathetic.engine.result.PathUtils;
import net.desolatesky.entity.DSLivingEntity;
import net.desolatesky.pathfinding.Pathfinding;
import net.desolatesky.pathfinding.WorldPathfindingContext;
import net.minestom.server.coordinate.Point;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.LinkedList;

public final class EntityNavigator<T extends DSLivingEntity<T>> {

    // chosen arbitrarily to check if an entity reached the target
    private static final double REACHED_TARGET_THRESHOLD = 0.25;
    private static final double MOVED_AWAY_FROM_TARGET_THRESHOLD = Math.pow(2, 2);

    private static final int UPDATE_PATH_TICK_COOLDOWN = 20;

    private final T entity;
    private final MovementStrategy<T> movementStrategy;

    private int updatePathCooldown = 0;

    private final LinkedList<PathPosition> path = new LinkedList<>();

    private boolean reachedFinalTarget = false;
    private @Nullable NavigationTarget<T> navigationTarget;
    private @Nullable Point positionTarget;

    public EntityNavigator(T entity, MovementStrategy<T> movementStrategy) {
        this.entity = entity;
        this.movementStrategy = movementStrategy;
    }

    public void setNewTarget(@Nullable NavigationTarget<T> target) {
        this.navigationTarget = target;
        if (this.navigationTarget == null) {
            this.path.clear();
            this.positionTarget = null;
            return;
        }
        this.positionTarget = target.getTargetPosition();
    }

    @ApiStatus.Internal
    public void tick() {
        if (this.navigationTarget == null) {
            return;
        }
        final Point newTarget = this.navigationTarget.getTargetPosition();
        if (!newTarget.equals(this.positionTarget)) {
            this.positionTarget = newTarget;
        }
        if (!this.path.isEmpty()) {
            final Point next = Pathfinding.pointFromPathPosition(this.path.peek());
            if (this.navigationTarget.reachedTarget(this.entity, next)) {
                this.path.pop();
            }
        }
        if (this.path.isEmpty()) {
            if (this.checkReachedTarget()) {
                return;
            }
            this.updatePath(this.navigationTarget.getTargetPosition());
            return;
        }
        final Point next = Pathfinding.pointFromPathPosition(this.path.peek());
        if (!Pathfinding.isTraversable(this.entity.world(), next)) {
            this.updatePath(this.navigationTarget.getTargetPosition());
        } else {
            this.movementStrategy.moveTo(this.entity, next);
        }
    }

    public boolean reachedTarget() {
        return this.reachedFinalTarget;
    }

    private void updatePath(Point target) {
        if (this.updatePathCooldown < UPDATE_PATH_TICK_COOLDOWN) {
            this.updatePathCooldown++;
            return;
        }
        this.updatePathCooldown = 0;
        final Point start = this.entity.getPosition();
        Pathfinding.PATHFINDER.findPath(Pathfinding.pathPositionFromPoint(start), Pathfinding.pathPositionFromPoint(target), new WorldPathfindingContext(this.entity.world()))
                .ifPresent(result -> {
                    // Do movement logic here
                    this.entity.acquirable().sync(_ -> {
                        this.path.clear();
                        this.path.addAll(PathUtils.mutatePositions(result.getPath(), p -> p.add(0.5, 0, 0.5)).collect());
                    });
                });
    }

    private boolean checkReachedTarget() {
        if (this.navigationTarget == null) {
            return false;
        }
        this.reachedFinalTarget = this.navigationTarget.reachedTarget(this.entity);
        return this.reachedFinalTarget;
    }
}
