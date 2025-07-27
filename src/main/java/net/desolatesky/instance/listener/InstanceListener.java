package net.desolatesky.instance.listener;

import net.desolatesky.block.handler.DSBlockHandler;
import net.desolatesky.instance.lobby.LobbyInstance;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.instance.block.Block;

public final class InstanceListener {

    private InstanceListener() {
        throw new UnsupportedOperationException();
    }

    public static final EventNode<Event> ROOT = EventNode.all("instance-listener")
            .addChild(EventNode.type("player-join", EventFilter.PLAYER)
                    .addListener(PlayerBlockBreakEvent.class, event -> {
                        final Block block = event.getBlock();
                        final DSBlockHandler blockHandler = (DSBlockHandler) block.handler();
                        if (blockHandler == null || blockHandler.isUnbreakable()) {
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
            );

    public static void register(EventNode<Event> root) {
        root.addChild(ROOT);
        root.addChild(LOBBY_NODE);
    }

}
