package net.desolatesky.entity;

import net.desolatesky.instance.DSInstance;
import net.desolatesky.instance.InstancePoint;
import net.kyori.adventure.key.Keyed;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.PlayerHand;

public interface DSEntity extends Keyed {

    DSInstance getDSInstance();

    void onClick(DSEntity clicker, Point interactionPoint, PlayerHand hand);

    void onPunch(DSEntity attacker);

    InstancePoint<? extends Point> getInstancePosition();

}
