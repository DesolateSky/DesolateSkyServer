package net.desolatesky.player.listener;

import net.desolatesky.Listener;
import net.desolatesky.util.InventoryUtil;
import net.minestom.server.component.DataComponents;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.item.PlayerFinishItemUseEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.item.component.Food;
import org.jetbrains.annotations.NotNullByDefault;

@NotNullByDefault
public final class PlayerEatListener implements Listener<PlayerEvent> {

    @Override
    public void register(EventNode<PlayerEvent> node) {
        node.addListener(PlayerFinishItemUseEvent.class, event -> {
            final Food food = event.getItemStack().get(DataComponents.FOOD);
            if (food != null) {
                final Player player = event.getPlayer();
                final float health = player.getHealth();
                event.getPlayer().setHealth(health + food.nutrition() * 0.5f);
                InventoryUtil.subtractFromHeldItem(player, event.getHand(), 1);
            }
        });
    }
}
