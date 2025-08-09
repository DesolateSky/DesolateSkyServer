package net.desolatesky.entity;

import net.desolatesky.instance.DSInstance;
import net.desolatesky.instance.InstancePoint;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.PlayerHand;

public interface DSEntity {

    DSInstance getInstance();

    void onClick(DSEntity clicker, Point interactionPoint, PlayerHand hand);

    void onPunch(DSEntity attacker);

    InstancePoint<? extends Point> getInstancePosition();

    EntityKey key();

}
