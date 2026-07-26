package net.desolatesky.entity.ai.navigation.movement;

import net.desolatesky.entity.DSLivingEntity;
import net.desolatesky.entity.ai.navigation.MovementStrategy;
import net.minestom.server.collision.CollisionUtils;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.utils.position.PositionUtils;

public final class WalkingStrategy<T extends DSLivingEntity<T>> implements MovementStrategy<T> {

    public WalkingStrategy() {
    }

    @Override
    public void moveTo(DSLivingEntity<T> entity, Point to) {
        final double speed = entity.getAttributeValue(Attribute.MOVEMENT_SPEED);
         moveTowards(entity, to, speed, to);
    }

    private static void moveTowards(Entity entity, Point direction, double speed, Point lookAt) {
        final Pos position = entity.getPosition();
        final double dx = direction.x() - position.x();
        final double dy = direction.y() - position.y();
        final double dz = direction.z() - position.z();

        final double dxLook = lookAt.x() - position.x();
        final double dyLook = lookAt.y() - position.y();
        final double dzLook = lookAt.z() - position.z();

        // the purpose of these few lines is to slow down entities when they reach their destination
        final double distSquared = dx * dx + dy * dy + dz * dz;
        if (speed > distSquared) {
            speed = distSquared;
        }

        final double radians = Math.atan2(dz, dx);
        final double speedX = Math.cos(radians) * speed;
        final double speedZ = Math.sin(radians) * speed;
        final float yaw = PositionUtils.getLookYaw(dxLook, dzLook);
        final float pitch = PositionUtils.getLookPitch(dxLook, dyLook, dzLook);

        final var physicsResult = CollisionUtils.handlePhysics(entity, new Vec(speedX, 0, speedZ));
        entity.refreshPosition(physicsResult.newPosition().asPos().withView(yaw, pitch));
    }


}
