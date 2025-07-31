package net.desolatesky.listener.type;

import net.desolatesky.block.DSBlock;
import net.desolatesky.entity.DSEntity;
import net.desolatesky.instance.team.TeamInstance;
import net.desolatesky.inventory.InventoryHolder;
import net.desolatesky.listener.DSEventHandler;
import net.desolatesky.listener.DSEventHandlers;
import net.desolatesky.listener.DSListener;
import net.desolatesky.listener.EventHandlerResult;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.team.role.RolePermissionType;
import net.minestom.server.entity.Entity;
import net.minestom.server.event.Event;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.event.player.PlayerUseItemOnBlockEvent;
import net.minestom.server.event.trait.BlockEvent;
import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.event.trait.PlayerEvent;
import org.jetbrains.annotations.Contract;

public final class ProtectionListener implements DSEventHandlers<Event> {

    public ProtectionListener() {
    }

    @Override
    @Contract("_ -> param1")
    public DSListener.Builder<Event> register(DSListener.Builder<Event> builder) {
        return builder.handler(PickupItemEvent.class, this.pickupItem())
                .handler(PlayerBlockBreakEvent.class, this.blockBreak())
                .handler(PlayerBlockBreakEvent.class, this.blockPlace())
                .handler(PlayerUseItemOnBlockEvent.class, this.useItemOnBlock())
                .handler(PlayerBlockInteractEvent.class, this.blockInteract())
                .handler(PlayerEntityInteractEvent.class, this.entityInteract());
    }

    private DSEventHandler<PickupItemEvent> pickupItem() {
        return event -> {
            final DSPlayer player = (DSPlayer) event.getEntity();
            if (!(event.getInstance() instanceof final TeamInstance instance)) {
                event.setCancelled(true);
                return EventHandlerResult.CONSUME_EVENT;
            }
            if (!instance.team().isMember(player.getUuid())) {
                event.setCancelled(true);
                return EventHandlerResult.CONSUME_EVENT;
            }
            final Entity entity = event.getEntity();
            if (!(entity instanceof final InventoryHolder inventoryHolder)) {
                return EventHandlerResult.CONTINUE_LISTENING;
            }
            inventoryHolder.getInventory().addItemStack(event.getItemStack());
            return EventHandlerResult.CONSUME_EVENT;
        };
    }

    private DSEventHandler<PlayerBlockBreakEvent> blockBreak() {
        return cancelBlockEventIfNoPermission(RolePermissionType.BREAK_BLOCK);
    }

    private DSEventHandler<PlayerBlockBreakEvent> blockPlace() {
        return cancelBlockEventIfNoPermission(RolePermissionType.PLACE_BLOCK);
    }

    private DSEventHandler<PlayerUseItemOnBlockEvent> useItemOnBlock() {
        return DSEventHandlers.passthroughIf(event -> {
            if (!(event.getInstance() instanceof final TeamInstance instance)) {
                return false;
            }
            return DSEventHandlers.hasPermission(instance, (DSPlayer) event.getPlayer(), RolePermissionType.INTERACT_BLOCK, DSBlock.getIdFor(instance.getBlock(event.getPosition())));
        });
    }

    private DSEventHandler<PlayerBlockInteractEvent> blockInteract() {
        return passthroughIfHasPermission(RolePermissionType.INTERACT_BLOCK);
    }

    private DSEventHandler<PlayerEntityInteractEvent> entityInteract() {
        return DSEventHandlers.passthroughIf(event -> {
            if (!(event.getInstance() instanceof final TeamInstance instance)) {
                return false;
            }
            if (!(event.getEntity() instanceof final DSEntity entity)) {
                return false;
            }
            return DSEventHandlers.hasPermission(instance, (DSPlayer) event.getPlayer(), RolePermissionType.INTERACT_ENTITY, entity.key().key());
        });
    }

    private static <E extends InstanceEvent & CancellableEvent & PlayerEvent & BlockEvent> DSEventHandler<E> cancelBlockEventIfNoPermission(RolePermissionType permissionType) {
        return DSEventHandlers.cancelIf(event -> !DSEventHandlers.hasPermission(event.getInstance(), (DSPlayer) event.getPlayer(), permissionType, DSBlock.getIdFor(event.getBlock())));
    }

    private static <E extends InstanceEvent & PlayerEvent & BlockEvent> DSEventHandler<E> passthroughIfHasPermission(RolePermissionType permissionType) {
        return DSEventHandlers.passthroughIf(event -> DSEventHandlers.hasPermission(event.getInstance(), (DSPlayer) event.getPlayer(), permissionType, DSBlock.getIdFor(event.getBlock())));
    }

}
