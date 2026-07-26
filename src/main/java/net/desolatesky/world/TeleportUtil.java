package net.desolatesky.world;

import net.desolatesky.player.DSPlayer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.instance.Instance;

import java.util.concurrent.CompletableFuture;

public final class TeleportUtil {

    public static CompletableFuture<Void> teleportEntity(Entity entity, Instance instance, Pos position) {
        if (instance.equals(entity.getInstance())) {
            return entity.teleport(position);
        }
        return entity.setInstance(instance, position);
    }

    public static CompletableFuture<Void> teleportToSpawn(DSPlayer player, DSWorld world) {
        return teleportEntity(player, world, world.getSpawnPointFor(player).asPos());
    }

    private TeleportUtil() {
    }
}
