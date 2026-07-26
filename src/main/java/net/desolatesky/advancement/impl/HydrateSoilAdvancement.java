package net.desolatesky.advancement.impl;

import net.desolatesky.advancement.IslandAdvancement;
import net.desolatesky.advancement.event.AdvancementCompleteEvent;
import net.desolatesky.block.HydrateSoilEvent;
import net.desolatesky.island.Island;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.server.DSServer;
import net.desolatesky.util.BlockUtil;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minestom.server.advancements.FrameType;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public final class HydrateSoilAdvancement extends IslandAdvancement {

    public HydrateSoilAdvancement(Key group, Key id, Component title, Component description, Material icon, FrameType frameType, float x, float y, @Unmodifiable List<Key> children) {
        super(group, id, title, description, icon, frameType, x, y, children);
    }

    @Override
    public boolean isCompleted(Island island) {
        return false;
    }

    @Override
    public boolean isCompleted(DSPlayer player) {
        return false;
    }

    @Override
    public void registerListener(DSServer server, EventNode<Event> node) {
        node.addListener(HydrateSoilEvent.class, event -> {
            final DSPlayer player = event.player();
            if (player == null) {
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
        });
    }
}
