package net.desolatesky.world;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.instance.Instance;

public final class TeleportUtil {

    public static void teleportEntity(Entity entity, Instance instance, Pos position) {
        if (instance.equals(entity.getInstance())) {
            entity.teleport(position);
            return;
        }
        entity.setInstance(instance, position);
    }

    private TeleportUtil() {

    }
}
