package net.desolatesky.advancement.impl;

import net.desolatesky.advancement.IslandAdvancement;
import net.desolatesky.advancement.event.AdvancementCompleteEvent;
import net.desolatesky.island.Island;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.server.DSServer;
import net.desolatesky.util.ItemUtil;
import net.desolatesky.util.event.ItemsAddedToInventoryEvent;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minestom.server.advancements.FrameType;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public final class CollectItemAdvancement extends IslandAdvancement {

    private final Key item;

    public CollectItemAdvancement(
            Key advancementGroup,
            Key advancementId,
            Component title,
            Component description,
            Material icon,
            FrameType frameType,
            float x,
            float y,
            @Unmodifiable List<Key> children,
            Key item
    ) {
        super(advancementGroup, advancementId, title, description, icon, frameType, x, y, children);
        this.item = item;
    }

    @Override
    public boolean isCompleted(Island island) {
        return false;
    }

    @Override
    public boolean isCompleted(DSPlayer player) {
        for (final ItemStack itemStack : player.getInventory().getItemStacks()) {
            if (ItemUtil.getItemId(itemStack).equals(this.item)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void registerListener(DSServer server, EventNode<Event> node) {
        node.addListener(PickupItemEvent.class, e -> {
            if (!(e.getEntity() instanceof final DSPlayer player)) {
                return;
            }
            this.check(server, player, e.getItemStack());
        });
        node.addListener(ItemsAddedToInventoryEvent.class, event -> {
            if (!(event.entity() instanceof final DSPlayer player)) {
                return;
            }
            for (final ItemStack itemStack : event.items()) {
                this.check(server, player, itemStack);
            }
        });
    }

    private void check(DSServer server, DSPlayer player, ItemStack itemStack) {
        if (!this.item.equals(ItemUtil.getItemId(itemStack))) {
            return;
        }
        if (!player.hasIsland()) {
            return;
        }
        final Island island = server.islandManager().getLoaded(player.getIslandId());
        if (island == null) {
            return;
        }
        EventDispatcher.call(new AdvancementCompleteEvent(this, island, player));
    }
}
