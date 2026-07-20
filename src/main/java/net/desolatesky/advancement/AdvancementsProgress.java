package net.desolatesky.advancement;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import net.desolatesky.island.Island;
import net.desolatesky.player.DSPlayer;
import net.kyori.adventure.key.Key;
import net.minestom.server.MinecraftServer;
import net.minestom.server.advancements.Advancement;
import net.minestom.server.advancements.AdvancementRoot;
import net.minestom.server.advancements.AdvancementTab;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class AdvancementsProgress {

    // advancement tab id -> advancements island is able to complete but have not yet been completed
    private final Multimap<Key, Key> currentAdvancements;
    private final List<AdvancementTab> tabs;

    public AdvancementsProgress(Multimap<Key, Key> currentAdvancements) {
        this.currentAdvancements = currentAdvancements;
        this.tabs = new ArrayList<>();
    }

    public void initialize(IslandAdvancementManager islandAdvancementManager, Island island) {
        if (this.currentAdvancements.isEmpty()) {
            islandAdvancementManager.getAdvancements().forEach(advancements -> this.currentAdvancements.put(advancements.key(), advancements.rootAdvancement()));
        }
        for (final Key groupKey : this.currentAdvancements.keys()) {
            final IslandAdvancements islandAdvancements = islandAdvancementManager.getAdvancements(groupKey);
            if (islandAdvancements == null) {
                continue;
            }
            final Key rootKey = islandAdvancements.rootAdvancement();
            final IslandAdvancement rootAdvancement = Objects.requireNonNull(islandAdvancements.getAdvancement(rootKey));
            final AdvancementRoot root = (AdvancementRoot) rootAdvancement.createAdvancement();
//            final AdvancementRoot root = new AdvancementRoot(
//                Component.text("test"),
//                Component.text("test"),
//                Material.DIRT,
//                FrameType.TASK,
//                0,
//                0,
//                null
//        );
            final AdvancementTab tab = MinecraftServer.getAdvancementManager().createTab(island.islandId() + "-" + groupKey.value(), root);
            this.traverseAdvancements(tab, root, islandAdvancements, groupKey, rootAdvancement, island);
            this.tabs.add(tab);
        }
    }

    public void addViewer(DSPlayer player) {
        if (this.tabs.isEmpty()) {
            throw new IllegalStateException("Advancements not initialized yet");
        }
//        final AdvancementManager advancementManager1 = MinecraftServer.getAdvancementManager();
//        final AdvancementTab tab = advancementManager1.createTab("test", new AdvancementRoot(
//                Component.text("test"),
//                Component.text("test"),
//                Material.DIRT,
//                FrameType.TASK,
//                0,
//                0,
//                null
//        ));
//        tab.addViewer(player);
        this.tabs.forEach(tab -> tab.addViewer(player));
    }

    public void removeViewer(DSPlayer player) {
        this.tabs.forEach(tab -> tab.removeViewer(player));
    }

    private void traverseAdvancements(
            AdvancementTab tab,
            Advancement parentAdvancement,
            IslandAdvancements islandAdvancements,
            Key advancementGroup,
            IslandAdvancement islandAdvancement,
            Island island
    ) {
        parentAdvancement.setAchieved(islandAdvancement.isCompleted(island));
        for (final Key childKey : islandAdvancement.children()) {
            final IslandAdvancement child = islandAdvancements.getAdvancement(childKey);
            if (child == null) {
                continue;
            }
            final Advancement advancement = child.createAdvancement();
            tab.createAdvancement(child.key.asString(), advancement, parentAdvancement);
            if (this.currentAdvancements.containsEntry(advancementGroup, childKey)) {
                continue;
            }
            // only add advancements if they have been completed or are currently worked on
            this.traverseAdvancements(
                    tab,
                    advancement,
                    islandAdvancements,
                    advancementGroup,
                    child,
                    island);
        }
    }

    public @Unmodifiable Multimap<Key, Key> currentAdvancements() {
        return Multimaps.unmodifiableMultimap(this.currentAdvancements);
    }
}
