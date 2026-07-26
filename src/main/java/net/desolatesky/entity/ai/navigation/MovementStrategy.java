package net.desolatesky.entity.ai.navigation;

import net.desolatesky.entity.DSLivingEntity;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;

public interface MovementStrategy<T extends DSLivingEntity<T>> {

    void moveTo(DSLivingEntity<T> entity, Point to);

}
