package net.desolatesky.entity;

import net.desolatesky.instance.DSInstance;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.PlayerHand;

public interface DSEntity  {

    DSInstance getDSInstance();

    void onClick(DSEntity clicker, Point interactionPoint, PlayerHand hand);

    void onPunch(DSEntity attacker);

}
