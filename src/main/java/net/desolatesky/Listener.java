package net.desolatesky;

import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import org.jetbrains.annotations.NotNullByDefault;

@NotNullByDefault
public interface Listener<T extends Event> {

    void register(EventNode<T> node);

}
