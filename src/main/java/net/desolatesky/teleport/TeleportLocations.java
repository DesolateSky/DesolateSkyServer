package net.desolatesky.teleport;

import net.desolatesky.util.Namespace;
import net.kyori.adventure.key.Key;

public final class TeleportLocations {

    private TeleportLocations() {
        throw new UnsupportedOperationException();
    }

    public static final Key ISLAND = Namespace.key("island");
    public static final Key SPAWN = Namespace.key("spawn");
    public static final Key HOME = Namespace.key("home");

}
