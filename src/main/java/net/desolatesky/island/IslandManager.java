package net.desolatesky.island;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Multimaps;
import net.desolatesky.advancement.AdvancementsProgress;
import net.desolatesky.advancement.IslandAdvancementManager;
import net.desolatesky.cooldown.DurationCooldown;
import net.desolatesky.data.FileDatabase;
import net.desolatesky.island.role.IslandRole;
import net.desolatesky.lock.Lockable;
import net.desolatesky.logging.DSLogger;
import net.desolatesky.message.MessageHandler;
import net.desolatesky.message.Messages;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.DateTimeUtil;
import net.desolatesky.world.PlayerWorld;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.EventDispatcher;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class IslandManager implements Lockable {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final FileDatabase<IslandSnapshot> islandDatabase;
    private final IslandAdvancementManager advancementManager;
    private final MessageHandler messageHandler;
    private final Cache<UUID, Island> islands = Caffeine.newBuilder()
            .expireAfterAccess(Duration.of(1, ChronoUnit.HOURS))
            .<UUID, Island>evictionListener((_, island, _) -> {
                EventDispatcher.call(new IslandUnloadEvent(island));
            })
            .build();

    public IslandManager(FileDatabase<IslandSnapshot> islandDatabase, IslandAdvancementManager advancementManager, MessageHandler messageHandler) {
        this.islandDatabase = islandDatabase;
        this.advancementManager = advancementManager;
        this.messageHandler = messageHandler;
    }

    public @Nullable Island getLoaded(UUID islandId) {
        return this.lockRead(() -> this.islands.getIfPresent(islandId));
    }

    public CompletableFuture<@Nullable Island> loadOrGet(UUID islandId) {
        return this.lockRead(() -> {
            final Island loaded = this.islands.getIfPresent(islandId);
            if (loaded != null) {
                return CompletableFuture.completedFuture(loaded);
            }
            return this.islandDatabase.loadData(islandId)
                    .thenApply(islandSnapshot -> {
                        if (islandSnapshot == null) {
                            return null;
                        }
                        final Island island = new DSIsland(islandSnapshot);
                        island.getAdvancementsProgress().checkProgress(this.advancementManager, island, null);
                        return this.lockWrite(() -> {
                            if (this.islands.asMap().containsKey(islandId)) {
                                DSLogger.getLogger().severe("Island already exists (" + islandId + ") when loading island.");
                                return null;
                            }
                            this.islands.put(island.islandId(), island);
                            this.initializeIsland(island);
                            return island;
                        });
                    });
        });
    }

    public CompletableFuture<@Nullable Island> createIsland(DSPlayer player) {
        final boolean allowed = this.lockWrite(() -> {
            if (player.isCreatingIsland()) {
                return false;
            }
            player.setCreatingIsland(true);
            return true;
        });
        if (!allowed || player.hasIsland()) {
            this.messageHandler.sendMessage(player, Messages.ALREADY_HAS_ISLAND);
            return CompletableFuture.completedFuture(null);
        }
        final DurationCooldown createCooldown = player.getIslandCreateCooldown();
        if (createCooldown != null && !createCooldown.isComplete()) {
            player.sendMessage(Component.text("You must wait before creating another island! Time left: " + DateTimeUtil.durationToString(createCooldown.getTimeLeft())));
            player.setCreatingIsland(false);
            return CompletableFuture.completedFuture(null);
        }
        final UUID islandId = UUID.randomUUID();
        return CompletableFuture.supplyAsync(() -> this.lockWrite(() -> {
                    final Map<UUID, IslandRole> islandRoles = new HashMap<>();
                    islandRoles.put(player.getUuid(), IslandRole.OWNER);
                    final DSIsland island = new DSIsland(
                            islandId,
                            islandRoles,
                            new HashMap<>(),
                            new AdvancementsProgress(Multimaps.newSetMultimap(new HashMap<>(), HashSet::new), Multimaps.newSetMultimap(new HashMap<>(), HashSet::new)),
                            Component.text(player.getUsername()).append(Component.text("'s Island")), PlayerWorld.STARTING_REGION);
                    this.lockWrite(() -> this.islands.put(islandId, island));
                    player.setIslandId(islandId);
                    player.setCreatingIsland(false);
                    return island;
                }))
                .thenApply(island -> {
                    if (island == null) {
                        this.messageHandler.sendMessage(player, Messages.ISLAND_CREATION_FAILED, Map.of("error-code", 0));
                        return null;
                    }
                    this.messageHandler.sendMessage(player, Messages.CREATED_ISLAND);
                    this.initializeIsland(island);
                    this.islandDatabase.saveDataNow(islandId, island.createSnapshot());
                    return island;
                });
    }

    public boolean deleteIsland(Island island) {
        this.islands.invalidate(island.islandId());
        return this.islandDatabase.deleteNow(island.islandId());
    }

    private void initializeIsland(Island island) {
        island.getAdvancementsProgress().initialize(this.advancementManager, island);
        for (final UUID memberId : island.getMembers()) {
            final DSPlayer player = (DSPlayer) MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(memberId);
            if (player == null) {
                continue;
            }
            island.getAdvancementsProgress().addViewer(player);
        }
        island.getAdvancementsProgress().checkProgress(this.advancementManager, island, null);
    }

    public @Unmodifiable Collection<Island> getAll() {
        return Set.copyOf(this.islands.asMap().values());
    }

    @Override
    public ReadWriteLock lock() {
        return this.lock;
    }
}
