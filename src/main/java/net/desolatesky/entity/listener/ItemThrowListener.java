package net.desolatesky.entity.listener;

import net.desolatesky.Listener;
import net.desolatesky.entity.IslandEntity;
import net.desolatesky.island.permission.IslandPermission;
import net.desolatesky.item.ItemTags;
import net.desolatesky.world.DSWorld;
import net.desolatesky.world.IslandWorld;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNullByDefault;

import java.time.Duration;

@NotNullByDefault
public final class ItemThrowListener implements Listener<Event> {

    @Override
    public void register(EventNode<Event> node) {
        node.addListener(ItemDropEvent.class, event -> {
            if (!(event.getInstance() instanceof final IslandWorld islandWorld)) {
                event.setCancelled(true);
                return;
            }
            final Player player = event.getPlayer();
            if (!islandWorld.island().hasPermission(player.getUuid(), IslandPermission.DROP_ITEMS)) {
                event.setCancelled(true);
                return;
            }
            if (!(islandWorld instanceof DSWorld world)) {
                event.setCancelled(true);
                return;
            }
            final ItemStack itemStack = event.getItemStack();
            final Vec direction = player.getPosition().direction().mul(0.5);
            final Key droppedItemEntityId = itemStack.getTag(ItemTags.DROPPED_ITEM_ENTITY_KEY);
            ItemEntity entity = null;
            if (droppedItemEntityId != null) {
                final IslandEntity islandEntity = world.entityFactory().createEntity(droppedItemEntityId, islandWorld.island(), _ -> {
                        })
                        .orElse(null);
                if (islandEntity instanceof final ItemEntity e) {
                    entity = e;
                    entity.setItemStack(itemStack);
                }
            } else {
                entity = new ItemEntity(itemStack);
            }
            if (entity == null) {
                return;
            }
            entity.setPickupDelay(Duration.ofSeconds(1));
            entity.setInstance(event.getInstance(), player.getPosition().add(direction.x(), player.getEyeHeight() - 0.4, direction.z()));
            entity.setVelocity(direction.mul(4 * 2));
        });
    }
}
