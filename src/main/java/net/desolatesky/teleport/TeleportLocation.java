package net.desolatesky.teleport;

import net.desolatesky.world.WorldType;
import net.minestom.server.coordinate.Point;
import org.jetbrains.annotations.UnknownNullability;

import java.util.UUID;

public record TeleportLocation(Type type, @UnknownNullability UUID islandId, UUID worldId, Point position, WorldType worldType) {

    public enum Type {
        ISLAND,
        SPAWN
    }
}
