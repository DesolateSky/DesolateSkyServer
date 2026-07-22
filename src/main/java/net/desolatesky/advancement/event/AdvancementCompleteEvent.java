package net.desolatesky.advancement.event;

import net.desolatesky.advancement.IslandAdvancement;
import net.desolatesky.island.Island;
import net.desolatesky.player.DSPlayer;
import net.minestom.server.event.Event;
import org.jetbrains.annotations.Nullable;

public record AdvancementCompleteEvent(
        IslandAdvancement islandAdvancement,
        Island island,
        @Nullable DSPlayer player
) implements Event {

    public AdvancementCompleteEvent(IslandAdvancement islandAdvancement, Island island) {
        this(islandAdvancement, island, null);
    }
}
