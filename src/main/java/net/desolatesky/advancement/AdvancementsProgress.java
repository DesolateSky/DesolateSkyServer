package net.desolatesky.advancement;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import net.desolatesky.island.Island;
import net.desolatesky.player.DSPlayer;
import net.kyori.adventure.key.Key;
import net.minestom.server.MinecraftServer;
import net.minestom.server.advancements.Advancement;
import net.minestom.server.advancements.AdvancementRoot;
import net.minestom.server.advancements.AdvancementTab;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class AdvancementsProgress {

    // advancement tab id -> advancements island is able to complete but have not yet been completed
    private final SetMultimap<Key, Key> currentAdvancements;
    /// advancement tab id -> advancement with no children
    private final SetMultimap<Key, Key> completedAdvancements;
    private final Map<Key, Advancement> advancements;
    private final Map<Key, AdvancementTab> tabs;

    public AdvancementsProgress(SetMultimap<Key, Key> currentAdvancements, SetMultimap<Key, Key> completedAdvancements) {
        this.currentAdvancements = currentAdvancements;
        this.completedAdvancements = completedAdvancements;
        this.advancements = new HashMap<>();
        this.tabs = new HashMap<>();
    }

    public void initialize(IslandAdvancementManager islandAdvancementManager, Island island) {
        if (this.currentAdvancements.isEmpty()) {
            islandAdvancementManager.getAdvancements().forEach(advancements -> this.currentAdvancements.put(advancements.key(), advancements.rootAdvancement()));
        }
        for (final Key groupKey : this.currentAdvancements.keySet()) {
            final IslandAdvancements islandAdvancements = islandAdvancementManager.getAdvancements(groupKey);
            if (islandAdvancements == null) {
                continue;
            }
            final Key rootKey = islandAdvancements.rootAdvancement();
            final IslandAdvancement rootAdvancement = Objects.requireNonNull(islandAdvancements.getAdvancement(rootKey));
            final AdvancementRoot root = (AdvancementRoot) rootAdvancement.createAdvancement();
            if (root == null) {
                continue;
            }
            final AdvancementTab tab = MinecraftServer.getAdvancementManager().createTab(createTabId(island, groupKey), root);
            root.showToast(false);
            root.setAchieved(true);
            this.advancements.put(rootKey, root);
            this.traverseAdvancements(tab, root, islandAdvancements, groupKey, rootAdvancement, !this.currentAdvancements.containsEntry(groupKey, rootKey));
            this.tabs.put(groupKey, tab);
        }
    }

    private static String createTabId(Island island, Key key) {
        return island.islandId() + "-" + key.value();
    }

    private void traverseAdvancements(
            AdvancementTab tab,
            Advancement parentAdvancement,
            IslandAdvancements islandAdvancements,
            Key advancementGroup,
            IslandAdvancement islandAdvancement,
            boolean completed
    ) {
        boolean nextCompleted = completed;
        if (this.completedAdvancements.containsEntry(advancementGroup, islandAdvancement.id())) {
            parentAdvancement.setAchieved(true);
            completed = true;
            nextCompleted = false;
            for (final Key childKey : islandAdvancement.children()) {
                this.currentAdvancements.put(advancementGroup, childKey);
            }
            if (!islandAdvancement.children().isEmpty()) {
                this.completedAdvancements.remove(advancementGroup, islandAdvancement.id());
            }
        }
        for (final Key childKey : islandAdvancement.children()) {
            final IslandAdvancement child = islandAdvancements.getAdvancement(childKey);
            if (child == null) {
                continue;
            }
            final Advancement advancement = child.createAdvancement();
            if (advancement == null) {
                continue;
            }
            this.advancements.put(childKey, advancement);
            nextCompleted = nextCompleted && (!this.currentAdvancements.containsEntry(advancementGroup, childKey) && !this.completedAdvancements.containsEntry(advancementGroup, childKey));
            advancement.showToast(false);
            if (!completed) {
                advancement.setHidden(true);
            } else {
                advancement.setAchieved(nextCompleted);
            }
            tab.createAdvancement(child.id().asString(), advancement, parentAdvancement);
            this.traverseAdvancements(
                    tab,
                    advancement,
                    islandAdvancements,
                    advancementGroup,
                    child,
                    nextCompleted
            );
        }
    }

    public void checkProgress(IslandAdvancementManager advancementManager, Island island, @Nullable DSPlayer player) {
        final Multimap<Key, Key> next = Multimaps.newListMultimap(new HashMap<>(), ArrayList::new);
        final Multimap<Key, Key> remove = Multimaps.newListMultimap(new HashMap<>(), ArrayList::new);
        boolean completed = false;
        for (final Map.Entry<Key, Key> entry : this.currentAdvancements.entries()) {
            final Key group = entry.getKey();
            final Key advancementId = entry.getValue();
            final IslandAdvancements advancementsGroup = advancementManager.getAdvancements(group);
            if (advancementsGroup == null) {
                continue;
            }
            final IslandAdvancement islandAdvancement = advancementsGroup.getAdvancement(advancementId);
            if (islandAdvancement == null) {
                continue;
            }
            if (!(player != null && islandAdvancement.isCompleted(player)) && !islandAdvancement.isCompleted(island)) {
                continue;
            }
            for (final Key child : islandAdvancement.children()) {
                next.put(group, child);
                final Advancement minecraftAdvancement = this.advancements.get(child);
                if (minecraftAdvancement != null) {
                    minecraftAdvancement.setHidden(false);
                }
            }
            remove.put(group, advancementId);
            completed = true;
            final Advancement advancement = this.advancements.get(advancementId);
            if (advancement != null) {
                advancement.showToast(false);
                advancement.setAchieved(true);
            }
        }
        remove.entries().forEach(entry -> this.currentAdvancements.remove(entry.getKey(), entry.getValue()));
        this.currentAdvancements.putAll(next);
        if (completed) {
            // check added advancements in case this advancement was one that was completed before the advancement
            // was created for the server
            this.checkProgress(advancementManager, island, null);
        }
    }

    public void completeAdvancement(IslandAdvancementManager advancementManager, IslandAdvancement advancement) {
        final Key group = advancement.group();
        final IslandAdvancements advancementsGroup = advancementManager.getAdvancements(group);
        if (advancementsGroup == null) {
            return;
        }
        final Key id = advancement.id();
        if (!this.currentAdvancements.containsEntry(group, id)) {
            return;
        }
        final Advancement minecraftAdvancement = this.advancements.get(id);
        if (minecraftAdvancement != null) {
            minecraftAdvancement.setHidden(false);
            minecraftAdvancement.showToast(true);
            minecraftAdvancement.setAchieved(true);
            minecraftAdvancement.showToast(false);
        }
        this.currentAdvancements.remove(group, id);
        this.currentAdvancements.putAll(group, advancement.children());
        if (advancement.children().isEmpty()) {
            this.completedAdvancements.put(group, id);
        }
        for (final Key child : advancement.children()) {
            final Advancement childAdvancement = this.advancements.get(child);
            if (childAdvancement != null) {
                childAdvancement.setHidden(false);
            }
        }
    }

    public void addViewer(DSPlayer player) {
        if (this.tabs.isEmpty()) {
            throw new IllegalStateException("Advancements not initialized yet");
        }
        this.tabs.values().forEach(tab -> tab.addViewer(player));
    }

    public void removeViewer(DSPlayer player) {
        this.tabs.values().forEach(tab -> tab.removeViewer(player));
    }

    public @Unmodifiable SetMultimap<Key, Key> getCurrentAdvancements() {
        return Multimaps.unmodifiableSetMultimap(this.currentAdvancements);
    }

    public @Unmodifiable SetMultimap<Key, Key> getCompletedAdvancements() {
        return Multimaps.unmodifiableSetMultimap(this.completedAdvancements);
    }
}
