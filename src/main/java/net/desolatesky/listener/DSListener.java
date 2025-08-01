package net.desolatesky.listener;

import com.google.common.collect.ListMultimap;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public final class DSListener<E extends Event> {

    private final String name;
    private final EventFilter<E, ?> filter;
    // make sure that the event handlers are ordered by their priority
    private final @Unmodifiable Map<Class<E>, List<DSEventHandler<E>>> eventHandlers;

    private DSListener(String name, EventFilter<E, ?> filter, Map<Class<E>, List<DSEventHandler<E>>> eventHandlers) {
        this.name = name;
        this.filter = filter;
        this.eventHandlers = Map.copyOf(eventHandlers);
    }

    public void register(EventNode<Event> node) {
        final EventNode<E> eventNode = EventNode.type(this.name, this.filter);
        for (final Class<E> eventClass : this.eventHandlers.keySet()) {
            final List<DSEventHandler<E>> handlers = this.eventHandlers.get(eventClass);
            eventNode.addListener(eventClass, event -> {
                for (final DSEventHandler<E> handler : handlers) {
                    final EventHandlerResult result = handler.handle(event);
                    if (result.consumes()) {
                        break;
                    }
                }
            });
        }
        node.addChild(eventNode);
    }

    public static <E extends Event> Builder<E> builder(String name, EventFilter<E, ?> filter) {
        return new Builder<>(name, filter);
    }

    public static class Builder<E extends Event> {

        private final String name;
        private final EventFilter<E, ?> filter;
        private final Map<Class<E>, List<DSEventHandler<E>>> eventHandlers = new HashMap<>();

        private Builder(String name, EventFilter<E, ?> filter) {
            this.name = name;
            this.filter = filter;
        }

        @SuppressWarnings("unchecked")
        public Builder<E> handler(Class<? extends E> eventClass, DSEventHandler<? extends E> handler) {
            this.eventHandlers.computeIfAbsent((Class<E>) eventClass, unused -> new ArrayList<>()).add((DSEventHandler<E>) handler);
            return this;
        }

        public Builder<E> handlers(Class<? extends E> eventClass, List<DSEventHandler<? extends E>> handlers) {
            handlers.forEach(handler -> this.handler(eventClass, handler));
            return this;
        }

        public Builder<E> handlers(ListMultimap<Class<? extends E>, DSEventHandler<? extends E>> handlers) {
            handlers.entries().forEach(entry -> {
                this.handler(entry.getKey(), entry.getValue());
            });
            return this;
        }

        public DSListener<E> build() {
            return new DSListener<>(this.name, this.filter, this.eventHandlers);
        }

    }

}
