package net.desolatesky.player.listener;

import net.desolatesky.Listener;
import net.desolatesky.logging.LoggerUtil;
import net.desolatesky.util.ItemUtil;
import net.kyori.adventure.text.Component;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.event.player.PlayerCommandEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNullByDefault;

@NotNullByDefault
public final class PlayerChatListener implements Listener<PlayerEvent> {

    @Override
    public void register(EventNode<PlayerEvent> node) {
        node.addListener(PlayerChatEvent.class, event -> {
            final ItemStack inHand = event.getPlayer().getItemInMainHand();
            event.setFormattedMessage(event.getFormattedMessage().replaceText(c -> {
                c.matchLiteral("[item]").replacement(
                        Component.text("[").append(
                                        ItemUtil.getItemName(inHand)
                                                .hoverEvent(event.getPlayer().getItemInMainHand().asHoverEvent()))
                                .append(Component.text("]"))
                );
            }));
            LoggerUtil.log(event.getPlayer().getUsername() + ": " + event.getRawMessage());
        });
        node.addListener(PlayerCommandEvent.class, event -> {
            LoggerUtil.log(event.getPlayer().getUsername() + ": " + event.getCommand());
        });
    }
}
