package net.desolatesky.entity;

import net.minestom.server.event.Event;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.event.trait.EntityEvent;

public final class EntityListener {

    private EntityListener() {
        throw new UnsupportedOperationException();
    }

    public static final EventNode<EntityEvent> ROOT = EventNode.type("entity-listener", EventFilter.ENTITY, (event, entity) -> entity instanceof DSEntity)
            .addListener(PlayerEntityInteractEvent.class, event -> {
                if (!(event.getTarget() instanceof final DSEntity target)) {
                    return;
                }
                target.onClick((DSEntity) event.getPlayer(), event.getInteractPosition(), event.getHand());
            })
            .addListener(EntityAttackEvent.class, event -> {
                if (!(event.getTarget() instanceof final DSEntity target)) {
                    return;
                }
                target.onPunch((DSEntity) event.getEntity());
            });

    public static void register(EventNode<Event> root) {
        root.addChild(ROOT);
    }

}
