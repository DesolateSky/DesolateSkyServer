package net.desolatesky.advancement.impl;

import net.desolatesky.advancement.IslandAdvancement;
import net.desolatesky.island.Island;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.server.DSServer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minestom.server.advancements.FrameType;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public final class CreateIslandAdvancement extends IslandAdvancement {

    public CreateIslandAdvancement(
            Key advancementGroup,
            Key advancementId,
            Component title,
            Component description,
            Material icon,
            FrameType frameType,
            float x,
            float y,
            @Unmodifiable List<Key> children
    ) {
        super(advancementGroup, advancementId, title, description, icon, frameType, x, y, children);
    }

    @Override
    public boolean isCompleted(Island island) {
        return true;
    }

    @Override
    public boolean isCompleted(DSPlayer player) {
        return player.hasIsland();
    }

    @Override
    public void registerListener(DSServer server, EventNode<Event> node) {
    }
}
