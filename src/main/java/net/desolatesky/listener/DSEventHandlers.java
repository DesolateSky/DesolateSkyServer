package net.desolatesky.listener;

import net.desolatesky.instance.team.TeamInstance;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.team.role.RolePermissionType;
import net.kyori.adventure.key.Key;
import net.minestom.server.event.Event;
import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.Contract;

import java.util.function.Function;
import java.util.function.Predicate;

public interface DSEventHandlers<E extends Event> {

    @Contract("_ -> param1")
    DSListener.Builder<Event> register(DSListener.Builder<Event> builder);

    static <T extends Event & CancellableEvent> EventHandlerResult cancel(T event) {
        event.setCancelled(true);
        return EventHandlerResult.CONSUME_EVENT;
    }

    static <T extends Event & CancellableEvent> DSEventHandler<T> cancelIf(Predicate<T> predicate) {
        return event -> {
            if (predicate.test(event)) {
                event.setCancelled(true);
                return EventHandlerResult.CONSUME_EVENT;
            }
            return EventHandlerResult.CONTINUE_LISTENING;
        };
    }

    static <T extends Event> DSEventHandler<T> passthroughIf(Predicate<T> predicate) {
        return event -> {
            if (predicate.test(event)) {
                return EventHandlerResult.CONTINUE_LISTENING;
            }
            return EventHandlerResult.CONSUME_EVENT;
        };
    }

    static <T extends Event & CancellableEvent> DSEventHandler<T> cancelIf(Predicate<T> predicate, Function<T, EventHandlerResult> successFunction) {
        return event -> {
            if (predicate.test(event)) {
                event.setCancelled(true);
                return successFunction.apply(event);
            }
            return successFunction.apply(event);
        };
    }

    static boolean hasPermission(Instance instance, DSPlayer player, RolePermissionType permissionType, Key permissionValue) {
        return instance instanceof final TeamInstance teamInstance && teamInstance.team().hasPermission(player, permissionType, permissionValue);
    }

    static boolean cancelIfNoTogglePermission(Instance instance, DSPlayer player, RolePermissionType permissionType) {
        return instance instanceof final TeamInstance teamInstance && teamInstance.team().hasTogglePermission(player, permissionType);
    }

}
