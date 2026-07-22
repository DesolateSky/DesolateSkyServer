package net.desolatesky.advancement;

import net.desolatesky.island.Island;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.server.DSServer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minestom.server.advancements.Advancement;
import net.minestom.server.advancements.FrameType;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public abstract class IslandAdvancement {

    protected final Key group;
    protected final Key id;

    protected final Component title;
    protected final Component description;
    protected final Material icon;
    protected final FrameType frameType;
    protected final float x;
    protected final float y;

    protected final @Unmodifiable List<Key> children;

    public IslandAdvancement(Key group, Key id, Component title, Component description, Material icon, FrameType frameType, float x, float y, @Unmodifiable List<Key> children) {
        this.group = group;
        this.id = id;
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.frameType = frameType;
        this.x = x;
        this.y = y;
        this.children = children;
    }


    public final Key group() {
        return this.group;
    }

    public final Key id() {
        return this.id;
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

    /**
     * This must call AdvancementCompleteEvent to notify that the advancement has been completed
     * @param node
     */
    public abstract void registerListener(DSServer server, EventNode<Event> node);
}
