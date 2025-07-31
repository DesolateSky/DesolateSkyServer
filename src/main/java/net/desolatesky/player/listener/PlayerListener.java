package net.desolatesky.player.listener;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.breaking.BreakingManager;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.instance.DSInstanceManager;
import net.desolatesky.instance.lobby.LobbyInstance;
import net.desolatesky.listener.DSEventHandler;
import net.desolatesky.listener.DSEventHandlers;
import net.desolatesky.listener.DSListener;
import net.desolatesky.listener.EventHandlerResult;
import net.desolatesky.pack.ResourcePackSettings;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.player.database.PlayerData;
import net.desolatesky.team.IslandTeam;
import net.desolatesky.util.Constants;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerCancelDiggingEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerFinishDiggingEvent;
import net.minestom.server.event.player.PlayerLoadedEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.event.player.PlayerStartDiggingEvent;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.server.play.RecipeBookSettingsPacket;
import net.minestom.server.ping.Status;
import net.minestom.server.utils.identity.NamedAndIdentified;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public final class PlayerListener implements DSEventHandlers<Event> {

    private final DesolateSkyServer server;
    private final ResourcePackSettings resourcePackSettings;
    private final byte[] favicon;

    public PlayerListener(DesolateSkyServer server, ResourcePackSettings resourcePackSettings, byte[] favicon) {
        this.server = server;
        this.resourcePackSettings = resourcePackSettings;
        this.favicon = favicon;
    }

    @Override
    public DSListener.Builder<Event> register(DSListener.Builder<Event> builder) {
        return builder.handler(AsyncPlayerConfigurationEvent.class, playerConfigurationEventHandler())
                .handler(PlayerLoadedEvent.class, this.playerLoadedEventHandler())
                .handler(ServerListPingEvent.class, this.serverListPingHandler())
                .handler(PlayerDisconnectEvent.class, this.playerDisconnectHandler())
                .handler(PlayerMoveEvent.class, this.playerMoveHandler());
    }


    private DSEventHandler<AsyncPlayerConfigurationEvent> playerConfigurationEventHandler() {
        return event -> {
            final DSInstanceManager instanceManager = this.server.instanceManager();
            final DSPlayer player = (DSPlayer) event.getPlayer();
            this.applyResourcePack(player);
            final PlayerData playerData = player.playerData();
            final Pos logoutPosition = playerData.lastLogoutPos();
            final UUID logoutInstanceId = playerData.lastLogoutInstanceId();
            final LobbyInstance lobbyInstance = instanceManager.lobbyInstance();
            player.setRespawnPoint(lobbyInstance.getDefaultSpawnPoint().pos());
            if (logoutPosition == null || logoutInstanceId == null) {
                event.setSpawningInstance(lobbyInstance);
                return EventHandlerResult.CONTINUE_LISTENING;
            }
            final IslandTeam islandTeam = this.server.islandTeamManager().getOrLoadTeam(player);
            final Instance instance = instanceManager.getOrLoadInstance(logoutInstanceId, islandTeam);
            if (!(instance instanceof final DSInstance dsInstance)) {
                event.setSpawningInstance(lobbyInstance);
                return EventHandlerResult.CONTINUE_LISTENING;
            }
            dsInstance.setPlayerSpawnPoint(player, logoutPosition);
            event.setSpawningInstance(instance);
            return EventHandlerResult.CONTINUE_LISTENING;
        };
    }

    private DSEventHandler<PlayerLoadedEvent> playerLoadedEventHandler() {
        return event -> {
            final DSPlayer player = (DSPlayer) event.getPlayer();
            player.setAllowFlying(true);
            player.setFlying(true);
            final RecipeBookSettingsPacket settingsPacket = new RecipeBookSettingsPacket(true, true, true, true, true, true, true, true);
            player.sendPacket(settingsPacket);
            player.refreshRecipes();
            return EventHandlerResult.CONTINUE_LISTENING;
        };
    }

    private DSEventHandler<ServerListPingEvent> serverListPingHandler() {
        return event -> {
            final int onlinePlayers = MinecraftServer.getConnectionManager().getOnlinePlayerCount();
            event.setStatus(Status.builder()
                    .description(
                            Component.text("Desolate Sky (Very Early Beta!)").color(Constants.PRIMARY_COLOR)
                                    .appendNewline()
                                    .append(Component.text("Join now to help with testing! (Use /discord)"))
                    )
                    .favicon(this.favicon)
                    .playerInfo(Status.PlayerInfo.builder()
                            .onlinePlayers(onlinePlayers)
                            .maxPlayers(50)
                            .sample(
                                    MinecraftServer.getConnectionManager().getOnlinePlayers()
                                            .stream()
                                            .filter(p -> p.getSettings().allowServerListings())
                                            .sorted((p1, p2) -> p1.getUsername().compareToIgnoreCase(p2.getUsername()))
                                            .map(p -> (NamedAndIdentified) p)
                                            .toList()
                            ).build())
                    .build());
            return EventHandlerResult.CONTINUE_LISTENING;
        };
    }

    private DSEventHandler<PlayerDisconnectEvent> playerDisconnectHandler() {
        return event -> {
            final DSPlayer player = (DSPlayer) event.getPlayer();
            this.server.instanceManager().handlePlayerLeaver(player);
            this.server.playerManager().forceSave(player);
            return EventHandlerResult.CONTINUE_LISTENING;
        };
    }

    private DSEventHandler<PlayerMoveEvent> playerMoveHandler() {
        return event -> {
            final DSPlayer player = (DSPlayer) event.getPlayer();
            final DSInstance instance = player.getDSInstance();
            if (player.getPosition().y() < -64) {
                player.teleport(instance.getSpawnPointFor(player));
            }
            return EventHandlerResult.CONTINUE_LISTENING;
        };
    }


    private void applyResourcePack(DSPlayer player) {
        final ResourcePackRequest request = ResourcePackRequest.resourcePackRequest()
                .packs(ResourcePackInfo.resourcePackInfo(this.resourcePackSettings.id(), this.resourcePackSettings.uri(), this.resourcePackSettings.hash()))
                .prompt(Component.text("Please accept the resource pack to continue."))
                .callback((id, status, audience) -> {
                    LoggerFactory.getLogger(PlayerListener.class).info("Resource pack {} for player was {}", id, status);
                })
                .required(true)
                .build();
        LoggerFactory.getLogger(PlayerListener.class).info("Applying resource pack: {} ({}) with hash: {}", this.resourcePackSettings.id(), this.resourcePackSettings.uri(), this.resourcePackSettings.hash());
        player.sendResourcePacks(request);
    }

}
