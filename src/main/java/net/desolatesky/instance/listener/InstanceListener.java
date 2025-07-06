package net.desolatesky.instance.listener;

import net.desolatesky.block.BlockTags;
import net.desolatesky.instance.lobby.LobbyInstance;
import net.desolatesky.player.DSPlayer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.instance.block.Block;

public final class InstanceListener {

    private InstanceListener() {
        throw new UnsupportedOperationException();
    }

    public static final EventNode<Event> ROOT = EventNode.all("instance-listener")
            .addChild(EventNode.type("player-join", EventFilter.PLAYER)
                    .addListener(PlayerBlockBreakEvent.class, event -> {
                        final Player player = event.getPlayer();
                        final Block block = event.getBlock();
                        final Boolean unbreakable = block.getTag(BlockTags.UNBREAKABLE);
                        if (unbreakable != null && unbreakable) {
                            event.setCancelled(true);
                            return;
                        }
                    })
            )
            .addChild(EventNode.type("set-block", EventFilter.INSTANCE));

    public static final EventNode<Event> LOBBY_NODE = EventNode.all("lobby-instance-listener")
            .addChild(EventNode.type("block-break", EventFilter.INSTANCE, (event, instance) -> instance instanceof LobbyInstance)
                    .addListener(PlayerBlockBreakEvent.class, event -> event.setCancelled(true))
                    .addListener(PlayerBlockPlaceEvent.class, event -> event.setCancelled(true))
                    .addListener(PlayerMoveEvent.class, event -> {
                        final LobbyInstance lobbyInstance = (LobbyInstance) event.getInstance();
                        final DSPlayer player = (DSPlayer) event.getPlayer();
                        if (player.getPosition().y() < -64) {
                            player.teleport(lobbyInstance.getSpawnPoint());
                        }
                    })
            );

    public static void register(EventNode<Event> root) {
        root.addChild(ROOT);
        root.addChild(LOBBY_NODE);
    }

}
