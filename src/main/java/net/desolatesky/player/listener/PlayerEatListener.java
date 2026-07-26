package net.desolatesky.player.listener;

import net.desolatesky.Listener;
import net.desolatesky.util.InventoryUtil;
import net.minestom.server.component.DataComponents;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.item.PlayerFinishItemUseEvent;
import net.minestom.server.event.trait.PlayerEvent;
import org.jetbrains.annotations.NotNullByDefault;

@NotNullByDefault
public final class PlayerEatListener implements Listener<PlayerEvent> {

    @Override
    public void register(EventNode<PlayerEvent> node) {
        node.addListener(PlayerFinishItemUseEvent.class, event -> {
            if (event.getItemStack().get(DataComponents.FOOD) != null) {
                final Player player = event.getPlayer();
                final float health = player.getHealth();
                event.getPlayer().setHealth(health + 1);
                InventoryUtil.subtractFromHeldItem(player, event.getHand(), 1);
            }
        });
    }
}
