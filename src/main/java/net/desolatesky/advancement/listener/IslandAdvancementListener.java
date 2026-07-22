package net.desolatesky.advancement.listener;

import net.desolatesky.Listener;
import net.desolatesky.advancement.IslandAdvancementManager;
import net.desolatesky.advancement.event.AdvancementCompleteEvent;
import net.desolatesky.island.Island;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import org.jetbrains.annotations.NotNullByDefault;

@NotNullByDefault
public final class IslandAdvancementListener implements Listener<Event> {

    private final IslandAdvancementManager islandAdvancementManager;

    public IslandAdvancementListener(IslandAdvancementManager islandAdvancementManager) {
        this.islandAdvancementManager = islandAdvancementManager;
    }

    @Override
    public void register(EventNode<Event> node) {
        node.addListener(AdvancementCompleteEvent.class, e -> {
            final Island island = e.island();
            island.getAdvancementsProgress().completeAdvancement(this.islandAdvancementManager, e.islandAdvancement());
        });
    }
}
