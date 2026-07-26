package net.desolatesky.command.informational;

import net.desolatesky.config.ConfigFile;
import net.desolatesky.util.Constants;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.minestom.server.command.builder.Command;

public final class DiscordCommand extends Command {

    public DiscordCommand(ConfigFile serverConfig) {
        super("discord", "support");

        this.setDefaultExecutor((sender, _) -> {
            final String serverDiscord = serverConfig.rootNode().node("discord").getString();
            if (serverDiscord != null) {
                sender.sendMessage(Component.text("Discord: ").color(Constants.ACCENT_COLOR)
                        .append(Component.text(serverDiscord).clickEvent(ClickEvent.openUrl(serverDiscord))
                                .color(Constants.TEXT_COLOR)));
            }
        });
    }
}
