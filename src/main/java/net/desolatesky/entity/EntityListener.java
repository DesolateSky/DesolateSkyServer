package net.desolatesky.entity;

import net.desolatesky.listener.DSEventHandler;
import net.desolatesky.listener.DSEventHandlers;
import net.desolatesky.listener.DSListener;
import net.desolatesky.listener.EventHandlerResult;
import net.minestom.server.event.Event;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.event.trait.EntityEvent;

public final class EntityListener implements DSEventHandlers<EntityEvent> {


    public EntityListener() {
    }

    @Override
    public DSListener.Builder<Event> register(DSListener.Builder<Event> builder) {
        return builder.handler(PlayerEntityInteractEvent.class, this.playerEntityInteractHandler())
                .handler(EntityAttackEvent.class, this.entityAttackHandler());
    }

    private DSEventHandler<PlayerEntityInteractEvent> playerEntityInteractHandler() {
        return event -> {
            if (!(event.getTarget() instanceof final DSEntity target)) {
                return EventHandlerResult.CONTINUE_LISTENING;
            }
            target.onClick((DSEntity) event.getPlayer(), event.getInteractPosition(), event.getHand());
            return EventHandlerResult.CONSUME_EVENT;
        };
    }

    private DSEventHandler<EntityAttackEvent> entityAttackHandler() {
        return event -> {
            if (!(event.getTarget() instanceof final DSEntity target)) {
                return EventHandlerResult.CONTINUE_LISTENING;
            }
            target.onPunch((DSEntity) event.getEntity());
            return EventHandlerResult.CONSUME_EVENT;
        };
    }

}
