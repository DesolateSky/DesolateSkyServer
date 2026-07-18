package net.desolatesky.player.listener;

import net.desolatesky.Listener;
import net.desolatesky.logging.LoggerUtil;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.event.trait.PlayerEvent;
import org.jetbrains.annotations.NotNullByDefault;

@NotNullByDefault
public final class PlayerChatListener implements Listener<PlayerEvent> {

    @Override
    public void register(EventNode<PlayerEvent> node) {
        node.addListener(PlayerChatEvent.class, event -> LoggerUtil.log(event.getPlayer().getUsername() + ": " + event.getRawMessage()));
    }
}
