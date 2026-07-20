package net.desolatesky.advancement.impl;

import net.desolatesky.advancement.IslandAdvancement;
import net.desolatesky.island.Island;
import net.desolatesky.player.DSPlayer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minestom.server.advancements.Advancement;
import net.minestom.server.advancements.AdvancementRoot;
import net.minestom.server.advancements.FrameType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public final class RootAdvancement extends IslandAdvancement {

    private final @Nullable String background;

    public RootAdvancement(Component title,
                           Component description,
                           Material icon,
                           FrameType frameType,
                           float x,
                           float y,
                           @Nullable String background,
                           Key key,
                           @Unmodifiable List<Key> children) {
        super(title, description, icon, frameType, x, y, key, children);
        this.background = background;
    }

    @Override
    public boolean isCompleted(Island island) {
        return true;
    }

    @Override
    public boolean isCompleted(DSPlayer player) {
        return true;
    }

    @Override
    public Advancement createAdvancement() {
        return new AdvancementRoot(this.title, this.description, this.icon, this.frameType, this.x, this.y, this.background);
    }
}
