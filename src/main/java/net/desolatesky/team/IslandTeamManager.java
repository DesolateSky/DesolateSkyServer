package net.desolatesky.team;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.RemovalCause;
import net.desolatesky.DesolateSkyServer;
import net.desolatesky.cooldown.Cooldowns;
import net.desolatesky.cooldown.PlayerCooldowns;
import net.desolatesky.instance.team.TeamInstance;
import net.desolatesky.message.MessageHandler;
import net.desolatesky.message.Messages;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.team.database.IslandCreationResult;
import net.desolatesky.team.database.IslandTeamDatabaseAccessor;
import net.desolatesky.util.TextUtil;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public final class IslandTeamManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(IslandTeamManager.class);

    private static final Duration DEFAULT_CACHE_EXPIRY = Duration.ofMinutes(5);

    public static final int MAX_TEAM_NAME_LENGTH = 32;

    private DesolateSkyServer server;
    private final IslandTeamDatabaseAccessor database;
    private final Cache<UUID, IslandTeam> teams;

    public IslandTeamManager(DesolateSkyServer server, IslandTeamDatabaseAccessor database, Cache<UUID, IslandTeam> teams) {
        this.server = server;
        this.database = database;
        this.teams = teams;
    }

    public IslandTeamManager(DesolateSkyServer server, IslandTeamDatabaseAccessor database) {
        this(server, database, Caffeine.newBuilder()
                .expireAfter(new Expiry<UUID, IslandTeam>() {
                    @Override
                    public long expireAfterCreate(@NonNull UUID key, @NonNull IslandTeam value, long currentTime) {
                        return DEFAULT_CACHE_EXPIRY.toNanos();
                    }

                    @Override
                    public long expireAfterUpdate(@NonNull UUID key, @NonNull IslandTeam value, long currentTime, @NonNegative long currentDuration) {
                        if (value.getOnlinePlayersCount() > 0) {
                            return DEFAULT_CACHE_EXPIRY.toNanos();
                        } else {
                            return currentDuration;
                        }
                    }

                    @Override
                    public long expireAfterRead(@NonNull UUID key, @NonNull IslandTeam value, long currentTime, @NonNegative long currentDuration) {
                        if (value.getOnlinePlayersCount() > 0) {
                            return DEFAULT_CACHE_EXPIRY.toNanos();
                        } else {
                            return currentDuration;
                        }
                    }
                })
                .evictionListener((@Nullable UUID key, @Nullable IslandTeam value, RemovalCause cause) -> {
                    if (key == null || value == null) {
                        return;
                    }
                    final IslandTeam.State state = value.state();
                    if (state.isDeleting() || state.isDeleted()) {
                        return;
                    }
                    if (cause.wasEvicted()) {
                        database.queueSave(key, value);
                    }
                })
                .build());
    }

    public @Nullable IslandTeam getTeam(UUID teamId) {
        return this.teams.getIfPresent(teamId);
    }

    public @Nullable IslandTeam getTeamByName(String name) {
        return this.teams.asMap().values().stream()
                .filter(team -> team.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public @Nullable IslandTeam getTeam(DSPlayer player) {
        final UUID islandId = player.islandId();
        if (islandId == null) {
            return null;
        }
        return this.getTeam(islandId);
    }

    public @Nullable IslandTeam getOrLoadTeam(DSPlayer player) {
        final UUID islandId = player.islandId();
        if (islandId == null) {
            LOGGER.info("Player {} does not have an island ID", player.getUsername());
            return null;
        }
        final IslandTeam team = this.getTeam(islandId);
        if (team != null) {
            LOGGER.info("Player {}'s team {} found in cache", player.getUsername(), islandId);
            return team;
        }
        LOGGER.info("Player {}'s team {} not found in cache, loading from database", player.getUsername(), islandId);
        final IslandTeam loadedTeam = Objects.requireNonNull(this.database.load(islandId), "Failed to load team from database");
        this.teams.put(loadedTeam.id(), loadedTeam);
        this.server.instanceManager().tryLoadIsland(loadedTeam);
        return loadedTeam;
    }

    public CompletableFuture<IslandCreationResult> createTeam(DSPlayer owner, String name) {
        final AtomicBoolean cooldown = new AtomicBoolean(true);
        DSPlayer.acquireAndSync(owner, unused -> {
            if (owner.cooldowns().isOnCooldown(Cooldowns.ISLAND_CREATION)) {
                this.server.messageHandler().sendMessage(owner, Messages.ISLAND_CREATE_COOLDOWN, Map.of("cooldown", TextUtil.formatDuration(owner.cooldowns().getCooldownTime(Cooldowns.ISLAND_CREATION))));
                cooldown.set(true);
            } else {
                cooldown.set(false);
            }
        });
        if (cooldown.get()) {
            return CompletableFuture.completedFuture(IslandCreationResult.ON_COOLDOWN);
        }
        return this.database.create(owner, name)
                .thenApply(result -> {
                    if (result.isInvalid()) {
                        return result;
                    }
                    final IslandTeam team = result.islandTeam();
                    if (team == null) {
                        return IslandCreationResult.DATABASE_ERROR;
                    }
                    final TeamInstance teamInstance = this.server.instanceManager().createIslandInstance(team);
                    teamInstance.save().join();
                    DSPlayer.acquireAndSync(owner, player -> {
                        player.cooldowns().addCooldown(Cooldowns.ISLAND_CREATION);
                        player.setIslandId(team.id());
                        this.server.playerManager().queueSave(player);
                    });
                    this.teams.put(team.id(), team);
                    return IslandCreationResult.createdInstance(team, teamInstance);
                });
    }

    public void deleteTeam(DSPlayer player, IslandTeam team) {
        final MessageHandler messageHandler = this.server.messageHandler();
        if (!team.isOwner(player)) {
            messageHandler.sendMessage(player, Messages.NOT_ISLAND_OWNER);
            return;
        }
        final int memberCount = team.getMemberCount();
        if (memberCount > 1) {
            messageHandler.sendMessage(player, Messages.ISLAND_NOT_EMPTY);
            return;
        }
        messageHandler.sendMessage(player, Messages.DELETING_ISLAND);
        final UUID islandId = team.id();
        this.database.delete(islandId, team)
                .thenAccept(unused -> {
                    this.teams.invalidate(islandId);
                    player.acquirable().sync(p -> {
                        player.getInventory().clear();
                        ((DSPlayer) p).setIslandId(null);
                    });
                    this.server.instanceManager().archiveTeamInstance(islandId);
                    messageHandler.sendMessage(player, Messages.DELETED_ISLAND);
                });
    }

    public void acceptInvite(IslandTeam team, DSPlayer player) {
        team.acceptInvite(this.server.messageHandler(), player, () -> {
            this.database.queueSave(team.id(), team);
            this.server.playerManager().queueSave(player);
        });
    }

    public void leave(IslandTeam team, DSPlayer player) {
        team.leave(this.server.messageHandler(), player, () -> {
            this.database.queueSave(team.id(), team);
            this.server.playerManager().queueSave(player);
        });
    }

    public @Unmodifiable Collection<IslandTeam> getAllTeams() {
        return this.teams.asMap().values().stream()
                .filter(team -> team.state() != IslandTeam.State.DELETED)
                .toList();
    }

    public void queueSave(IslandTeam islandTeam) {
        this.database.queueSave(islandTeam.id(), islandTeam);
    }

    public void forceSave(IslandTeam islandTeam) {
        this.database.save(islandTeam.id(), islandTeam);
    }

    private IslandTeamDatabaseAccessor database() {
        return this.database;
    }

    public void shutdown() {
        this.database.shutdown();
        this.teams.invalidateAll();
        LOGGER.info("IslandTeamManager shutdown complete.");
    }

}
