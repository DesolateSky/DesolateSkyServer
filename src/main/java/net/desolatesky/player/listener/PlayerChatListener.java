package net.desolatesky.player.listener;

import net.desolatesky.Listener;
import net.desolatesky.logging.DSLogger;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.profanity.ProfanityFilter;
import net.desolatesky.util.ComponentUtil;
import net.desolatesky.util.ItemUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.event.player.PlayerCommandEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.List;

@NotNullByDefault
public final class PlayerChatListener implements Listener<PlayerEvent> {

    private final ProfanityFilter profanityFilter;

    public PlayerChatListener(List<String> badWords) {
        this.profanityFilter = new ProfanityFilter(badWords);
    }

    @Override
    public void register(EventNode<PlayerEvent> node) {
        node.addListener(PlayerChatEvent.class, event -> {
            if (!(event.getPlayer() instanceof final DSPlayer player)) {
                event.setCancelled(true);
                return;
            }
            final ItemStack inHand = event.getPlayer().getItemInMainHand();
            String filteredMessage = this.profanityFilter.filter(event.getRawMessage());
            if (filteredMessage == null) {
                filteredMessage = event.getRawMessage();
            }
            final Component message = player.getDisplayName().append(Component.text(": ").style(Style.empty()))
                    .append(ComponentUtil.safeParse(filteredMessage));
            event.setFormattedMessage(message.replaceText(c -> c.matchLiteral("[item]").replacement(
                    Component.text("[").append(
                                    ItemUtil.getItemName(inHand)
                                            .hoverEvent(event.getPlayer().getItemInMainHand().asHoverEvent()))
                            .append(Component.text("]"))
            )));
            DSLogger.getLogger().info(event.getPlayer().getUsername() + ": " +
                    ComponentUtil.serialize(event.getFormattedMessage()));
        });
        node.addListener(PlayerCommandEvent.class, event -> {
            DSLogger.getLogger().info(event.getPlayer().getUsername() + ": /" + event.getCommand());
        });
    }

}
