package net.desolatesky.advancement;

import net.desolatesky.island.Island;
import net.desolatesky.player.DSPlayer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minestom.server.advancements.Advancement;
import net.minestom.server.advancements.FrameType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public abstract class IslandAdvancement {

    protected final Component title;
    protected final Component description;
    protected final Material icon;
    protected final FrameType frameType;
    protected final float x;
    protected final float y;

    protected final Key key;
    protected final @Unmodifiable List<Key> children;

    public IslandAdvancement(Component title, Component description, Material icon, FrameType frameType, float x, float y, Key key, @Unmodifiable List<Key> children) {
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.frameType = frameType;
        this.x = x;
        this.y = y;
        this.key = key;
        this.children = children;
    }

    public final Key key() {
        return this.key;
    }

    public abstract boolean isCompleted(Island island);

    public abstract boolean isCompleted(DSPlayer player);

    @Unmodifiable
    public final List<Key> children() {
        return this.children;
    }

    public Advancement createAdvancement() {
        return new Advancement(this.title, this.description, ItemStack.of(this.icon), this.frameType, this.x, this.y, false);
    }
}
