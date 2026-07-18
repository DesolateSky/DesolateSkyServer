package net.desolatesky.entity.listener;

import net.desolatesky.Listener;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.InventoryUtil;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.inventory.TransactionOption;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNullByDefault;

@NotNullByDefault
public final class ItemPickupListener implements Listener<Event> {

    @Override
    public void register(EventNode<Event> node) {
        node.addListener(PickupItemEvent.class, event -> {
           if (!(event.getEntity() instanceof final DSPlayer player)) {
               return;
           }
           final ItemStack result = player.getInventory().addItemStack(event.getItemStack(), TransactionOption.ALL);
           if (result.isAir()) {
               return;
           }
           event.setCancelled(true);
           event.getItemEntity().setItemStack(result);
        });
    }
}
