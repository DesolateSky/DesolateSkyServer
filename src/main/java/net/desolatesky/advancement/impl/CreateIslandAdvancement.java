package net.desolatesky.advancement.impl;

import net.desolatesky.advancement.IslandAdvancement;
import net.desolatesky.island.Island;
import net.desolatesky.player.DSPlayer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minestom.server.advancements.FrameType;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public final class CreateIslandAdvancement extends IslandAdvancement {

    public CreateIslandAdvancement(Component title,
                                   Component description,
                                   Material icon,
                                   FrameType frameType,
                                   float x,
                                   float y,
                                   Key key,
                                   @Unmodifiable List<Key> children) {
        super(title, description, icon, frameType, x, y, key, children);
    }

    @Override
    public boolean isCompleted(Island island) {
        return true;
    }

    @Override
    public boolean isCompleted(DSPlayer player) {
        return player.hasIsland();
    }
}
