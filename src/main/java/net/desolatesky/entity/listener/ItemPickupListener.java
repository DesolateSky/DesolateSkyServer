package net.desolatesky.entity.listener;

import net.desolatesky.Listener;
import net.desolatesky.cooldown.CooldownCollection;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.InventoryUtil;
import net.desolatesky.util.ItemUtil;
import net.desolatesky.util.MinecraftUtil;
import net.desolatesky.util.TimeUtil;
import net.minestom.server.component.DataComponent;
import net.minestom.server.component.DataComponents;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.attribute.AttributeInstance;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.PlayerChangeHeldSlotEvent;
import net.minestom.server.event.player.PlayerHandAnimationEvent;
import net.minestom.server.event.player.PlayerSwapItemEvent;
import net.minestom.server.inventory.TransactionOption;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.component.AttributeList;
import net.minestom.server.item.component.UseCooldown;
import org.jetbrains.annotations.NotNullByDefault;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

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
        node.addListener(PlayerChangeHeldSlotEvent.class, event -> {
            if (!(event.getPlayer() instanceof final DSPlayer player)) {
                return;
            }
            MinecraftUtil.resetHeldItemCooldown(player);
        });
        final EventNode<PlayerHandAnimationEvent> handAnimationNode = EventNode.type("hand-animation", EventFilter.from(PlayerHandAnimationEvent.class, Player.class, PlayerHandAnimationEvent::getPlayer));
        handAnimationNode.addListener(PlayerHandAnimationEvent.class, event -> {
            if (!(event.getPlayer() instanceof final DSPlayer player)) {
                return;
            }
           MinecraftUtil.resetHeldItemCooldown(player);
        });
        handAnimationNode.setPriority(Integer.MAX_VALUE);
        node.addChild(handAnimationNode);

    }
}
