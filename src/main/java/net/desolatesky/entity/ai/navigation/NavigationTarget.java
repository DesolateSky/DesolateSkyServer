package net.desolatesky.entity.ai.navigation;

import net.desolatesky.entity.DSLivingEntity;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import org.jetbrains.annotations.NotNullByDefault;

@NotNullByDefault
public sealed interface NavigationTarget<T extends DSLivingEntity<T>> {

    Point getTargetPosition();

    boolean reachedTarget(T entity);

    boolean reachedTarget(T entity, Point target);

    static <T extends DSLivingEntity<T>> NavigationTarget<T> createTarget(Point point, double distanceFromTargetThreshold) {
        return new FixedTarget<>(point, distanceFromTargetThreshold);
    }

    static <T extends DSLivingEntity<T>> NavigationTarget<T> createTarget(Entity entity, double distanceFromTargetThreshold) {
        return new EntityTarget<>(entity, distanceFromTargetThreshold);
    }

    final class FixedTarget<T extends DSLivingEntity<T>> implements NavigationTarget<T> {

        private final Point point;
        private final double distanceFromTargetThreshold;

        public FixedTarget(Point point, double distanceFromTargetThreshold) {
            this.point = point;
            this.distanceFromTargetThreshold = Math.pow(distanceFromTargetThreshold, 2);
        }

        @Override
        public Point getTargetPosition() {
            return this.point;
        }

        @Override
        public boolean reachedTarget(T entity) {
            return entity.getPosition().distanceSquared(this.point) < this.distanceFromTargetThreshold;
        }

        @Override
        public boolean reachedTarget(T entity, Point target) {
            return entity.getPosition().distanceSquared(target) < this.distanceFromTargetThreshold;
        }
    }

    final class EntityTarget<T extends DSLivingEntity<T>> implements NavigationTarget<T> {

        private final Entity entity;
        private final double distanceFromTargetThreshold;

        public EntityTarget(Entity entity, double distanceFromTargetThreshold) {
            this.entity = entity;
            this.distanceFromTargetThreshold = Math.pow(distanceFromTargetThreshold, 2);
        }

        @Override
        public Point getTargetPosition() {
            return this.entity.getPosition();
        }

        @Override
        public boolean reachedTarget(T entity) {
            return entity.getPosition().distanceSquared(this.entity.getPosition()) < this.distanceFromTargetThreshold;
        }

        @Override
        public boolean reachedTarget(T entity, Point target) {
            return entity.getPosition().distanceSquared(target) < this.distanceFromTargetThreshold;
        }
    }
}
