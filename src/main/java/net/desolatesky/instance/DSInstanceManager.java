package net.desolatesky.instance;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.instance.lobby.LobbyInstance;
import net.desolatesky.instance.region.Region;
import net.desolatesky.instance.team.TeamInstance;
import net.desolatesky.message.Messages;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.teleport.TeleportLocations;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.WorldBorder;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.UUID;

public final class DSInstanceManager {
    public static final UUID LOBBY_WORLD_ID = new UUID(0, 0);

    private static final double DIAMETER = 100;

    private final Path worldFolderPath;
    private LobbyInstance lobbyInstance;
    private final InstanceManager instanceManager;

    public DSInstanceManager(Path worldFolderPath, InstanceManager instanceManager) {
        this.worldFolderPath = worldFolderPath;
        this.instanceManager = instanceManager;
    }

    public void createLobbyInstance() {
        final Path lobbyWorldPath = this.worldFolderPath.resolve("lobby");
        final Pos spawn = new Pos(0, 64, 0);
        this.lobbyInstance = LobbyInstance.createLobby(LOBBY_WORLD_ID, lobbyWorldPath, spawn, Region.square(spawn, DIAMETER));
        this.lobbyInstance.setWorldBorder(new WorldBorder(DIAMETER, spawn.x(), spawn.z(), 0, 0, (int) DIAMETER));
        MinecraftServer.getInstanceManager().registerInstance(this.lobbyInstance);
    }

    public @Nullable TeamInstance getPlayerIsland(DSPlayer player) {
        final UUID islandId = player.islandId();
        if (islandId == null) {
            return null;
        }
        final Instance instance = this.instanceManager.getInstance(islandId);
        if (!(instance instanceof final TeamInstance teamInstance)) {
            return null;
        }
        return teamInstance;
    }

    public Instance getOrLoadInstance(UUID worldId) {
        final Instance instance = this.instanceManager.getInstance(worldId);
        if (instance != null) {
            return instance;
        }
        if (worldId.equals(LOBBY_WORLD_ID)) {
            return this.lobbyInstance;
        }
        final TeamInstance teamInstance = TeamInstance.load(this.instanceManager, worldId, this.worldFolderPath);
        if (teamInstance == null) {
            return this.lobbyInstance;
        }
        return teamInstance;
    }

    public void unloadPlayerIsland(DSPlayer player) {
        if (!player.hasIsland()) {
            return;
        }
        final TeamInstance teamInstance = this.getPlayerIsland(player);
        if (teamInstance != null) {
            teamInstance.unload().whenComplete((result, error) -> MinecraftServer.getSchedulerManager().scheduleEndOfTick(() -> {
                if (!teamInstance.getPlayers().isEmpty()) {
                    return;
                }
                this.instanceManager.unregisterInstance(teamInstance);
            }));
        } else {
            throw new IllegalStateException("Player has an island instance but it could not be found: " + player.islandId());
        }
    }

    public TeamInstance createIslandInstance(DSPlayer player) {
        if (player.hasIsland()) {
            final Instance instance = this.instanceManager.getInstance(player.getUuid());
            if (instance instanceof final TeamInstance teamInstance) {
                return teamInstance;
            }
            throw new IllegalStateException("Player has an island instance but it could not be found: " + player.getUuid());
        }
        final UUID playerUuid = player.getUuid();
        final TeamInstance teamInstance = TeamInstance.create(this.instanceManager, playerUuid, this.worldFolderPath);
        player.setIsland(teamInstance);
        player.sendIdMessage(Messages.CREATED_ISLAND);
        return teamInstance;
    }

    public @Nullable TeamInstance tryLoadPlayerIsland(DSPlayer player) {
        final UUID islandId = player.islandId();
        if (islandId == null) {
            return null;
        }
        final Instance loadedInstance = this.instanceManager.getInstance(islandId);
        if (loadedInstance instanceof final TeamInstance teamInstance) {
            player.setIsland(teamInstance);
            return teamInstance;
        }
        final TeamInstance instance = TeamInstance.load(this.instanceManager, islandId, this.worldFolderPath);
        if (instance == null) {
            return null;
        }
        player.setIsland(instance);
        return instance;
    }

    public void teleportToIsland(DSPlayer player) {
        final TeamInstance teamInstance = this.getPlayerIsland(player);
        if (teamInstance == null) {
            return;
        }
        this.teleportToIsland(player, teamInstance);
    }

    public void teleportToIsland(DSPlayer player, TeamInstance teamInstance) {
        final DesolateSkyServer server = DesolateSkyServer.get();
        server.teleportManager().queue(TeleportLocations.ISLAND, player, teamInstance.getSpawnPoint());
    }

    public UUID getLobbyWorldId() {
        return LOBBY_WORLD_ID;
    }

    public LobbyInstance lobbyInstance() {
        return this.lobbyInstance;
    }

    public InstancePos getLobbySpawnPos() {
        return this.lobbyInstance.getSpawnPoint();
    }

}
