package com.fisherl.desolatesky.teleport;

import net.minestom.server.coordinate.Point;

import java.util.UUID;

public record TeleportLocation(Type type, UUID worldId, Point position) {

    public enum Type {
        ISLAND,
        SPAWN
    }
}
