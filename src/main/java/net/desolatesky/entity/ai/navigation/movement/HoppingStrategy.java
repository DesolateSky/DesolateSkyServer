package net.desolatesky.entity.ai.navigation.movement;

import net.desolatesky.entity.DSLivingEntity;
import net.desolatesky.entity.ai.navigation.MovementStrategy;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.attribute.Attribute;

public final class HoppingStrategy<T extends DSLivingEntity<T>> implements MovementStrategy<T> {

    private final double height;

    public HoppingStrategy(double height) {
        this.height = height;
    }

    @Override
    public void moveTo(DSLivingEntity<T> entity, Point to) {
        if (!entity.isOnGround()) {
            return;
        }
        final double speed = entity.getAttributeValue(Attribute.MOVEMENT_SPEED);
        final Vec vec = to.sub(entity.getPosition()).asVec().normalize()
                .mul(speed * 10)
                .withY(this.height * 2.5f);
        entity.setVelocity(entity.getVelocity().add(vec));
        entity.lookAt(to);
//        entity.refreshPosition(entity.getPosition().add(vec));
    }
}
