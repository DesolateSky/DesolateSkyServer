package net.desolatesky.item.listener;

import net.desolatesky.Listener;
import net.desolatesky.item.ItemFactory;
import net.desolatesky.item.behavior.ClickBehavior;
import net.desolatesky.item.behavior.ItemBehavior;
import net.desolatesky.item.definition.ItemDefinition;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.world.DSWorld;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.event.player.PlayerUseItemOnBlockEvent;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNullByDefault;

@NotNullByDefault
public final class ItemClickListener implements Listener<Event> {

    private final ItemFactory itemFactory;

    public ItemClickListener(ItemFactory itemFactory) {
        this.itemFactory = itemFactory;
    }

    @Override
    public void register(EventNode<Event> node) {
        this.registerItemClick(node);
    }

    private void registerItemClick(EventNode<Event> node) {
        node.addListener(PlayerUseItemEvent.class, event -> {
           final ItemDefinition itemDefinition = this.itemFactory.getItemDefinition(event.getItemStack());
           if (itemDefinition == null) {
               return;
           }
           final ClickBehavior clickBehavior = itemDefinition.getBehavior(ItemBehavior.Type.CLICK);
           if (clickBehavior == null) {
               return;
           }
           if (!(event.getPlayer() instanceof final DSPlayer player)) {
               return;
           }
           if (!(player.getInstance() instanceof final DSWorld world)) {
               return;
           }
           clickBehavior.onClick(this.itemFactory, world, player, event.getHand(), event.getItemStack(), null, null);
        });
        node.addListener(PlayerUseItemOnBlockEvent.class, event -> {
            final ItemStack heldItem = event.getItemStack();
            final DSPlayer player = (DSPlayer) event.getPlayer();
            if (!(player.getInstance() instanceof final DSWorld world)) {
                return;
            }
            final ItemDefinition itemDefinition = this.itemFactory.getItemDefinition(heldItem);
            if (itemDefinition == null) {
                return;
            }
            final net.desolatesky.item.behavior.ClickBehavior itemClickBehavior = itemDefinition.getBehavior(ItemBehavior.Type.CLICK);
            if (itemClickBehavior == null) {
                return;
            }
            itemClickBehavior.onClick(this.itemFactory, world, player, event.getHand(), heldItem, event.getPosition(), world.getBlock(event.getPosition()));
        });
    }
}
