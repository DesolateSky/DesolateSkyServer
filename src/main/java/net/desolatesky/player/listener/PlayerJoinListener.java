package net.desolatesky.player.listener;

import net.desolatesky.Listener;
import net.desolatesky.config.ConfigFile;
import net.desolatesky.data.FileDatabase;
import net.desolatesky.island.Island;
import net.desolatesky.island.IslandManager;
import net.desolatesky.island.IslandSnapshot;
import net.desolatesky.item.ItemFactory;
import net.desolatesky.item.ItemIds;
import net.desolatesky.item.definition.ItemDefinition;
import net.desolatesky.logging.DSLogger;
import net.desolatesky.message.MessageHandler;
import net.desolatesky.message.Messages;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.player.DSPlayerData;
import net.desolatesky.server.ServerDatabases;
import net.desolatesky.server.player.PlayerBanDatabase;
import net.desolatesky.util.InventoryUtil;
import net.desolatesky.world.DSWorld;
import net.desolatesky.world.IslandWorld;
import net.desolatesky.world.WorldManager;
import net.desolatesky.world.pos.WorldPosition;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerLoadedEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.event.trait.PlayerEvent;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@NotNullByDefault
public class PlayerJoinListener implements Listener<PlayerEvent> {

    private final ServerDatabases serverDatabases;
    private final FileDatabase<DSPlayerData> playerDatabase;
    private final FileDatabase<IslandSnapshot> islandDatabase;
    private final WorldManager worldManager;
    private final IslandManager islandManager;
    private final ItemFactory itemFactory;
    private final MessageHandler messageHandler;
    private final ConfigFile serverConfig;

    public PlayerJoinListener(
            ServerDatabases serverDatabases,
            FileDatabase<DSPlayerData> playerDatabase,
            FileDatabase<IslandSnapshot> islandDatabase,
            WorldManager worldManager,
            IslandManager islandManager,
            ItemFactory itemFactory,
            MessageHandler messageHandler,
            ConfigFile serverConfig
    ) {
        this.serverDatabases = serverDatabases;
        this.playerDatabase = playerDatabase;
        this.islandDatabase = islandDatabase;
        this.worldManager = worldManager;
        this.islandManager = islandManager;
        this.itemFactory = itemFactory;
        this.messageHandler = messageHandler;
        this.serverConfig = serverConfig;
    }

    @Override
    public void register(EventNode<PlayerEvent> eventHandler) {
        this.registerJoinServer(eventHandler);
        this.registerJoinInstance(eventHandler);
        this.registerPlayerLoad(eventHandler);
        this.registerDisconnect(eventHandler);
    }

    private void registerJoinServer(EventNode<PlayerEvent> eventHandler) {
        eventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            if (!(event.getPlayer() instanceof final DSPlayer player)) {
                return;
            }
            final PlayerBanDatabase.Ban ban = this.serverDatabases.banDatabase().getPlayerBanNow(player.getUuid());
            if (ban != null) {
                final String bannerName = this.serverDatabases.playerUUIDDatabase().getPlayerName(ban.bannerUuid()).join();
                final String discord = Objects.requireNonNullElse(this.serverConfig.rootNode().node("discord").getString(), "https://discord.gg/uYqyQ6NWwJ");
                ban.kick(player, bannerName, discord);
                return;
            }
            this.serverDatabases.playerUUIDDatabase().savePlayerUUID(player.getUsername(), player.getUuid());
            final WorldPosition logoutPos = player.getLogoutPos();
            DSWorld world = null;
            if (logoutPos != null) {
                world = this.worldManager.loadWorld(logoutPos.islandId(), logoutPos.worldType()).join();
                player.setRespawnPoint(logoutPos.pos().asPos());
            }
            if (world == null) {
                world = this.worldManager.getLobbyWorld().join();
                player.setRespawnPoint(world.getSpawnPointFor(player).asPos());
            }
            player.setLogoutPos(null);
            event.setSpawningInstance(world);
            player.updateDisplayName();
        });
        eventHandler.addListener(PlayerSpawnEvent.class, event -> {
            if (!event.isFirstSpawn()) {
                return;
            }
            if (!(event.getPlayer() instanceof final DSPlayer player)) {
                return;
            }
            if (player.newPlayer()) {
                this.messageHandler.sendMessage(player, Messages.NEW_PLAYER_JOIN);
                for (final Player other : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
                    if (other.equals(player)) {
                        continue;
                    }
                    this.messageHandler.sendMessage(other, Messages.NEW_PLAYER_JOIN_WELCOME, Map.of("player", player.getDisplayName()));
                }
            } else {
                this.messageHandler.sendMessage(player, Messages.PLAYER_JOIN);
                for (final Player other : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
                    if (other.equals(player)) {
                        continue;
                    }
                    this.messageHandler.sendMessage(other, Messages.PLAYER_JOIN_WELCOME_BACK, Map.of("player", player.getDisplayName()));
                }
            }
        });
    }

    private void registerJoinInstance(EventNode<PlayerEvent> eventHandler) {
        eventHandler.addListener(PlayerSpawnEvent.class, event -> {
            final DSPlayer player = (DSPlayer) event.getPlayer();
            if (event.getInstance() instanceof final DSWorld world) {
                world.sendWorldBorder(player);
            }
            this.playerDatabase.saveData(player.getUuid(), player.createSnapshot());
            if (!(event.getInstance() instanceof final IslandWorld world)) {
                return;
            }
            this.islandDatabase.saveData(world.island().islandId(), world.island().createSnapshot());
            player.setAllowFlying(true);
        });
    }

    private void registerPlayerLoad(EventNode<PlayerEvent> node) {
        node.addListener(PlayerLoadedEvent.class, event -> {
            final DSPlayer player = (DSPlayer) event.getPlayer();
            final UUID islandId = player.getIslandId();
            if (!(event.getInstance() instanceof final DSWorld world)) {
                return;
            }
            if (islandId != null) {
                this.islandManager.loadOrGet(islandId).thenAccept(island -> {
                    if (island != null) {
                        island.onMemberJoinInstance(player, world);
                    }
                });
            }
        });
    }

    private void registerDisconnect(EventNode<PlayerEvent> eventHandler) {
        eventHandler.addListener(PlayerDisconnectEvent.class, event -> {
            final DSPlayer player = (DSPlayer) event.getPlayer();
            DSLogger.getLogger().info("Player left: " + player.getUsername());
            for (final Player other : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
                if (other.equals(player)) {
                    continue;
                }
                this.messageHandler.sendMessage(other, Messages.PLAYER_LEAVE, Map.of("player", player.getName()));
            }
            player.setLogoutPos(player.getWorldPosition());
            this.playerDatabase.saveData(player.getUuid(), player.createSnapshot());
            final UUID islandID = player.getIslandId();
            if (islandID == null) {
                return;
            }
            final Island island = this.islandManager.getLoaded(islandID);
            if (island == null) {
                return;
            }
            if (event.getInstance() instanceof final DSWorld world) {
                island.onMemberLeaveServer(player, world);
            }
            this.islandDatabase.saveData(islandID, island.createSnapshot());
        });
    }
}
