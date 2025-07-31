package net.desolatesky.listener;

import net.desolatesky.DesolateSkyServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.trait.InstanceEvent;

public final class DSListeners {

    public static void register(DesolateSkyServer server, EventNode<? extends Event> root) {

    }


    private DSListener<InstanceEvent> instanceListener() {
        return DSListener.builder("instance-listener", EventFilter.INSTANCE)
                .build();
    }

}
