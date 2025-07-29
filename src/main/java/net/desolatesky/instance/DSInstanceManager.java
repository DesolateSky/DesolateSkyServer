package net.desolatesky.instance;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.instance.lobby.LobbyInstance;
import net.desolatesky.instance.region.Region;
import net.desolatesky.instance.team.TeamInstance;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.team.IslandTeam;
import net.desolatesky.teleport.TeleportLocations;
import net.desolatesky.teleport.TeleportManager;
import net.desolatesky.util.FileUtil;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.WorldBorder;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

public final class DSInstanceManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DSInstanceManager.class);

    public static final UUID LOBBY_WORLD_ID = new UUID(0, 0);

    private static final double DIAMETER = 100;

    private final DesolateSkyServer server;
    private final Path worldFolderPath;
    private LobbyInstance lobbyInstance;
    private final InstanceManager instanceManager;

    public DSInstanceManager(DesolateSkyServer server, Path worldFolderPath, InstanceManager instanceManager) {
        this.server = server;
        this.worldFolderPath = worldFolderPath;
        this.instanceManager = instanceManager;
    }

    public void createLobbyInstance() {
        final Path lobbyWorldPath = this.worldFolderPath.resolve("lobby");
        final Pos spawn = new Pos(0, 64, 0);
        this.lobbyInstance = LobbyInstance.createLobby(this.server, LOBBY_WORLD_ID, lobbyWorldPath, spawn, Region.square(spawn, DIAMETER));
        this.lobbyInstance.setWorldBorder(new WorldBorder(DIAMETER, spawn.x(), spawn.z(), 0, 0, (int) DIAMETER));
        MinecraftServer.getInstanceManager().registerInstance(this.lobbyInstance);
    }

    public @Nullable TeamInstance getIslandInstance(IslandTeam team, boolean load) {
        if (load) {
            return this.tryLoadIsland(team);
        }
        final Instance instance = this.instanceManager.getInstance(team.id());
        if (!(instance instanceof final TeamInstance teamInstance)) {
            return this.tryLoadIsland(team);
        }
        return teamInstance;
    }

    public void archiveTeamInstance(UUID islandId) {
        final Instance instance = this.instanceManager.getInstance(islandId);
        if (instance instanceof TeamInstance teamInstance) {
            teamInstance.getPlayers().forEach(player -> {
                player.setInstance(this.lobbyInstance);
                this.server.playerManager().queueSave((DSPlayer) player);
            });
            teamInstance.unload().thenRun(() -> {
                final File file = teamInstance.worldFilePath().toFile();
                if (file.exists()) {
                    final File archiveFolder = this.worldFolderPath.resolve("archive").toFile();
                    if (!archiveFolder.exists()) {
                        archiveFolder.mkdirs();
                    }
                    final Path archivedWorld = teamInstance.worldFilePath().resolve("../");
                    try {
                        FileUtil.zipDirectory(archivedWorld, archiveFolder.toPath().resolve(islandId + ".zip"));
                        FileUtil.deleteDirectory(archivedWorld);
                    } catch (IOException e) {
                        LOGGER.info("Error archiving world file for world {}", teamInstance.getUuid());
                        e.printStackTrace();
                    }
                } else {
                    LOGGER.warn("Attempted to archive non-existent island instance: {}", islandId);
                }
            });
        }
    }

    public @Nullable TeamInstance getIslandInstance(UUID islandId) {
        final Instance instance = this.instanceManager.getInstance(islandId);
        if (!(instance instanceof final TeamInstance teamInstance)) {
            return null;
        }
        return teamInstance;
    }

    public Instance getOrLoadInstance(UUID worldId, @Nullable IslandTeam islandTeam) {
        final Instance instance = this.instanceManager.getInstance(worldId);
        if (instance != null) {
            return instance;
        }
        if (worldId.equals(LOBBY_WORLD_ID)) {
            return this.lobbyInstance;
        }
        if (islandTeam == null) {
            return this.lobbyInstance;
        }
        final TeamInstance teamInstance = TeamInstance.load(this.server, this.instanceManager, islandTeam, this.worldFolderPath);
        if (teamInstance == null) {
            return this.lobbyInstance;
        }
        return teamInstance;
    }

    public Instance getOrLoadInstance(IslandTeam islandTeam) {
        return this.getOrLoadInstance(islandTeam.id(), islandTeam);
    }

    public void handlePlayerLeaver(DSPlayer player) {
        if (!player.hasIsland()) {
            return;
        }
        final UUID islandId = player.islandId();
        if (islandId == null) {
            return;
        }
        final TeamInstance teamInstance = this.getIslandInstance(islandId);
        if (teamInstance != null) {
            teamInstance.onLeave(player);
            if (MinecraftServer.getConnectionManager().getOnlinePlayers().stream().noneMatch(p -> islandId.equals(((DSPlayer) p).islandId()) && !p.equals(player))) {
                teamInstance.save().whenComplete((_, _) -> MinecraftServer.getSchedulerManager().scheduleEndOfTick(() -> {
                    if (!teamInstance.getPlayers().isEmpty()) {
                        return;
                    }
                    this.instanceManager.unregisterInstance(teamInstance);
                }));
            }
        } else {
            throw new IllegalStateException("Player has an island instance but it could not be found: " + player.islandId());
        }
    }

    public TeamInstance createIslandInstance(IslandTeam team) {
        final Instance loadedInstance = this.getOrLoadInstance(team);
        if (loadedInstance instanceof final TeamInstance teamInstance) {
            return teamInstance;
        }
        return TeamInstance.create(this.server, this.instanceManager, team, this.worldFolderPath);
    }

    public @Nullable TeamInstance tryLoadIsland(IslandTeam islandTeam) {
        final UUID islandId = islandTeam.id();
        final Instance loadedInstance = this.instanceManager.getInstance(islandId);
        if (loadedInstance instanceof final TeamInstance teamInstance) {
            return teamInstance;
        }
        return TeamInstance.load(this.server, this.instanceManager, islandTeam, this.worldFolderPath);
    }

    public void teleportToIsland(TeleportManager teleportManager, IslandTeam team, DSPlayer player) {
        final TeamInstance teamInstance = this.getIslandInstance(team, true);
        if (teamInstance == null) {
            return;
        }
        this.teleportToIsland(teleportManager, player, teamInstance);
    }

    public void teleportToIsland(TeleportManager teleportManager, DSPlayer player, TeamInstance teamInstance) {
        teleportManager.queue(TeleportLocations.ISLAND, player, teamInstance.getSpawnPointFor(player));
    }

    public UUID getLobbyWorldId() {
        return LOBBY_WORLD_ID;
    }

    public LobbyInstance lobbyInstance() {
        return this.lobbyInstance;
    }

    public InstancePoint<Pos> getLobbySpawnPos() {
        return this.lobbyInstance.getDefaultSpawnPoint();
    }

}
