package net.desolatesky.listener;

import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;

public interface DSListener {

    void register(EventNode<Event> node);

}
