package net.desolatesky.entity.ai.navigation;

import net.desolatesky.entity.DSLivingEntity;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.Objects;

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

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof FixedTarget<?> that)) return false;
            return Double.compare(this.distanceFromTargetThreshold, that.distanceFromTargetThreshold) == 0 && Objects.equals(this.point, that.point);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.point, this.distanceFromTargetThreshold);
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

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof EntityTarget<?> that)) return false;
            return Double.compare(this.distanceFromTargetThreshold, that.distanceFromTargetThreshold) == 0 && Objects.equals(this.entity, that.entity);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.entity, this.distanceFromTargetThreshold);
        }
    }
}
