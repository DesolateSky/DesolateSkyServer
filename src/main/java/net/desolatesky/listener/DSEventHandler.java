package net.desolatesky.listener;

import net.minestom.server.event.Event;

public interface DSEventHandler<E extends Event> {

    EventHandlerResult handle(E event);

}
