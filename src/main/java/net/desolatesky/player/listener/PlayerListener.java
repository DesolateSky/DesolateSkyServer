package net.desolatesky.player.listener;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.breaking.BreakingManager;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.instance.DSInstanceManager;
import net.desolatesky.instance.InstancePoint;
import net.desolatesky.instance.lobby.LobbyInstance;
import net.desolatesky.item.DSItems;
import net.desolatesky.listener.DSListener;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.player.database.PlayerData;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerCancelDiggingEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerFinishDiggingEvent;
import net.minestom.server.event.player.PlayerLoadedEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.event.player.PlayerStartDiggingEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.server.play.RecipeBookSettingsPacket;

import java.util.UUID;

public final class PlayerListener implements DSListener {

    private final DesolateSkyServer server;

    public PlayerListener(DesolateSkyServer server) {
        this.server = server;
    }

    @Override
    public void register(EventNode<Event> root) {
        root.addChild(
                EventNode.all("instance-listener")
                        .addChild(this.playerJoinNode())
                        .addChild(this.playerDisconnectNode())
                        .addChild(this.playerDigNode())
                        .addChild(this.playerMoveNode())
        );
    }

    private EventNode<PlayerEvent> playerJoinNode() {
        return EventNode.type("player-join", EventFilter.PLAYER, (event, player) -> player instanceof DSPlayer)
                .addListener(AsyncPlayerConfigurationEvent.class, event -> {
                    final DSInstanceManager instanceManager = this.server.instanceManager();
                    final DSPlayer player = (DSPlayer) event.getPlayer();
                    final PlayerData playerData = player.playerData();
                    final Pos logoutPosition = playerData.lastLogoutPos();
                    final UUID logoutInstanceId = playerData.lastLogoutInstanceId();
                    final LobbyInstance lobbyInstance = instanceManager.lobbyInstance();
                    player.setRespawnPoint(lobbyInstance.getDefaultSpawnPoint().pos());
                    if (logoutPosition == null || logoutInstanceId == null) {
                        event.setSpawningInstance(lobbyInstance);
                        return;
                    }
                    final Instance instance = instanceManager.getOrLoadInstance(logoutInstanceId);
                    if (!(instance instanceof final DSInstance dsInstance)) {
                        event.setSpawningInstance(lobbyInstance);
                        return;
                    }
                    dsInstance.setPlayerSpawnPoint(player, logoutPosition);
                    event.setSpawningInstance(instance);
                })
                .addListener(PlayerLoadedEvent.class, event -> {
                    final DSPlayer player = (DSPlayer) event.getPlayer();
                    player.setAllowFlying(true);
                    player.setFlying(true);
                    if (!player.playerData().playedBefore()) {
                        player.getInventory().addItemStack(DSItems.DEBRIS_CATCHER.create());
                    }
                    final RecipeBookSettingsPacket settingsPacket = new RecipeBookSettingsPacket(true, true, true, true, true, true, true, true);
                    player.sendPacket(settingsPacket);
                    player.refreshRecipes();
                })
                .addListener(ItemDropEvent.class, event -> event.setCancelled(true));
    }

    private EventNode<PlayerEvent> playerDisconnectNode() {
        return EventNode.type("player-disconnect", EventFilter.PLAYER, (event, player) -> player instanceof DSPlayer)
                .addListener(PlayerDisconnectEvent.class, event -> {
                    final DSPlayer player = (DSPlayer) event.getPlayer();
                    this.server.instanceManager().handlePlayerLeaver(player);
                    this.server.playerManager().savePlayer(player);
                });
    }

    private EventNode<PlayerEvent> playerDigNode() {
        return EventNode.type("player-dig", EventFilter.PLAYER, (event, player) -> player instanceof DSPlayer)
                .addListener(PlayerStartDiggingEvent.class, event -> {
                    final DSPlayer player = (DSPlayer) event.getPlayer();
                    final DSInstance instance = player.getDSInstance();
                    if (instance == null) {
                        return;
                    }
                    if (!instance.canBreakBlock(player, event.getBlockPosition(), event.getBlock())) {
                        return;
                    }
                    final BreakingManager breakingManager = instance.breakingManager();
                    breakingManager.startBreaking(player, event.getBlockPosition(), event.getBlock());
                })
                .addListener(PlayerCancelDiggingEvent.class, event -> pauseBreaking((DSPlayer) event.getPlayer(), event.getBlockPosition()))
                .addListener(PlayerFinishDiggingEvent.class, event -> pauseBreaking((DSPlayer) event.getPlayer(), event.getBlockPosition()));
    }

    private EventNode<PlayerEvent> playerMoveNode() {
        return EventNode.type("player-move", EventFilter.PLAYER, (event, player) -> player instanceof DSPlayer)
                .addListener(PlayerMoveEvent.class, event -> {
                    final DSPlayer player = (DSPlayer) event.getPlayer();
                    final DSInstance instance = player.getDSInstance();
                    if (player.getPosition().y() < -64) {
                        player.teleport(instance.getSpawnPointFor(player));
                    }
                });
    }


    private static void pauseBreaking(DSPlayer player, BlockVec pos) {
        final DSInstance instance = player.getDSInstance();
        if (instance == null) {
            return;
        }
        final BreakingManager breakingManager = instance.breakingManager();
        breakingManager.pauseBreaking(player, pos);
    }

}
