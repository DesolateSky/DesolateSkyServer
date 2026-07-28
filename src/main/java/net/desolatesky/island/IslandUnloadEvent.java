package net.desolatesky.island;

import net.minestom.server.event.Event;

public record IslandUnloadEvent(Island island) implements Event {

}
