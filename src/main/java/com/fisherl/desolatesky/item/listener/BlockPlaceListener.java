package com.fisherl.desolatesky.item.listener;

import com.fisherl.desolatesky.Listener;
import com.fisherl.desolatesky.item.ItemFactory;
import com.fisherl.desolatesky.item.behavior.ItemBehavior;
import com.fisherl.desolatesky.player.DSPlayer;
import com.fisherl.desolatesky.world.DSWorld;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNullByDefault;

@NotNullByDefault
public final class BlockPlaceListener implements Listener<Event> {

    private final ItemFactory itemFactory;

    public BlockPlaceListener(ItemFactory itemFactory) {
        this.itemFactory = itemFactory;
    }

    @Override
    public void register(EventNode<Event> node) {
        node.addListener(PlayerBlockPlaceEvent.class, event -> {
            if (!(event.getEntity() instanceof final DSPlayer player)) {
                return;
            }
            if (!(event.getInstance() instanceof final DSWorld world)) {
                return;
            }
           final ItemStack itemStack = player.getItemInHand(event.getHand());
            this.itemFactory.getItemDefinition(itemStack)
                    .flatMap(def -> def.getBehavior(ItemBehavior.Type.BLOCK_PLACE))
                    .ifPresent(behavior -> {
                        if (!behavior.canPlace(world, player, itemStack, event.getBlockPosition())) {
                            event.setCancelled(true);
                        }
                    });
        });
    }
}
