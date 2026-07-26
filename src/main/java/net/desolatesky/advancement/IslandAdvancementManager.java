package net.desolatesky.advancement;

import net.desolatesky.config.ConfigFile;
import net.desolatesky.server.DSServer;
import net.kyori.adventure.key.Key;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class IslandAdvancementManager {

    private final List<IslandAdvancements> advancements;

    public void initialize(DSServer server) {
        final Path advancementsFolder = Path.of("advancements");
        final List<IslandAdvancements> islandAdvancements = new ArrayList<>();
        try {
            Files.walkFileTree(advancementsFolder, new FileVisitor<>() {
                @Override
                public @NotNull FileVisitResult preVisitDirectory(Path dir, @NotNull BasicFileAttributes attrs) {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public @NotNull FileVisitResult visitFile(Path file, @NotNull BasicFileAttributes attrs) {
                    final ConfigFile config = ConfigFile.get(file, "");
                    islandAdvancements.add(IslandAdvancements.load(config));
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public @NotNull FileVisitResult visitFileFailed(Path file, @NotNull IOException exc) {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public @NotNull FileVisitResult postVisitDirectory(Path dir, @Nullable IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.advancements.addAll(islandAdvancements);
        this.advancements.sort(Comparator.comparingInt(IslandAdvancements::index));
        final EventNode<Event> node = EventNode.all("advancements-listener");
        node.setPriority(Integer.MAX_VALUE);
        MinecraftServer.getGlobalEventHandler().addChild(node);
        this.advancements.forEach(advancements -> {
            advancements.getAllAdvancements().forEach(a -> a.registerListener(server, node));
        });
    }

    public IslandAdvancementManager() {
        this.advancements = new ArrayList<>();
    }

    public @Unmodifiable List<IslandAdvancements> getAdvancements() {
        return this.advancements;
    }

    public @Nullable IslandAdvancements getAdvancements(Key key) {
        for (final IslandAdvancements advancements : this.advancements) {
            if (advancements.key().equals(key)) {
                return advancements;
            }
        }
        return null;
    }
}
