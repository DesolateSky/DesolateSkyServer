package net.desolatesky.player.listener;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.breaking.BreakingManager;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.instance.DSInstanceManager;
import net.desolatesky.instance.InstancePos;
import net.desolatesky.player.DSPlayer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerCancelDiggingEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerFinishDiggingEvent;
import net.minestom.server.event.player.PlayerLoadedEvent;
import net.minestom.server.event.player.PlayerStartDiggingEvent;

public final class PlayerListener {

    private PlayerListener() {
        throw new UnsupportedOperationException();
    }

    public static final EventNode<Event> ROOT = EventNode.all("instance-listener")
            .addChild(EventNode.type("player-join", EventFilter.PLAYER, (event, player) -> player instanceof DSPlayer)
                    .addListener(AsyncPlayerConfigurationEvent.class, event -> {
                        final DesolateSkyServer server = DesolateSkyServer.get();
                        final DSInstanceManager instanceManager = server.instanceManager();
                        event.setSpawningInstance(instanceManager.lobbyInstance());
                    })
                    .addListener(PlayerLoadedEvent.class, event -> {
                        final DSPlayer player = (DSPlayer) event.getPlayer();
                        player.setAllowFlying(true);
                        player.setFlying(true);
                        final DesolateSkyServer server = DesolateSkyServer.get();
                        final DSInstanceManager instanceManager = server.instanceManager();
                        final InstancePos instancePos = server.playerManager().getPlayerLogoutPos(
                                player.getUuid(),
                                instanceManager::getOrLoadInstance
                        );
                        player.getAttribute(Attribute.BLOCK_BREAK_SPEED).setBaseValue(0);
                        if (instancePos == null) {
                            return;
                        }
                        player.teleport(instancePos);
                    })
                    .addListener(PlayerDisconnectEvent.class, event -> {
                        final DSPlayer player = (DSPlayer) event.getPlayer();
                        DesolateSkyServer.get().instanceManager().unloadPlayerIsland(player);
                    })
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
                    .addListener(PlayerFinishDiggingEvent.class, event -> pauseBreaking((DSPlayer) event.getPlayer(), event.getBlockPosition()))
            );

    private static void pauseBreaking(DSPlayer player, BlockVec pos) {
        final DSInstance instance = player.getDSInstance();
        if (instance == null) {
            return;
        }
        final BreakingManager breakingManager = instance.breakingManager();
        breakingManager.pauseBreaking(player, pos);
    }

    public static void register(EventNode<Event> root) {
        root.addChild(ROOT);
    }


}
