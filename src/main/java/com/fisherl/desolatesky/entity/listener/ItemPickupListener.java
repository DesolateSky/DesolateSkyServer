package com.fisherl.desolatesky.entity.listener;

import com.fisherl.desolatesky.Listener;
import com.fisherl.desolatesky.player.DSPlayer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.item.PickupItemEvent;
import org.jetbrains.annotations.NotNullByDefault;

@NotNullByDefault
public final class ItemPickupListener implements Listener<Event> {

    @Override
    public void register(EventNode<Event> node) {
        node.addListener(PickupItemEvent.class, event -> {
           if (!(event.getEntity() instanceof final DSPlayer player)) {
               return;
           }
           player.getInventory().addItemStack(event.getItemStack());
        });
    }
}
