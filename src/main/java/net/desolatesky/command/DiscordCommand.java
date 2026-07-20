package net.desolatesky.command;

import net.desolatesky.config.ConfigFile;
import net.desolatesky.util.Constants;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.minestom.server.command.builder.Command;

public final class DiscordCommand extends Command {

    public DiscordCommand(ConfigFile serverConfig) {
        super("discord", "support", "help");

        this.setDefaultExecutor((sender, _) -> {
            final String serverDiscord = serverConfig.rootNode().node("discord").getString();
            final String developmentDiscord = serverConfig.rootNode().node("development-discord").getString();
            if (serverDiscord != null) {
                sender.sendMessage(Component.text("Main Discord: ")
                        .append(Component.text(serverDiscord).clickEvent(ClickEvent.openUrl(serverDiscord))
                                .color(Constants.PRIMARY_COLOR)));
            }
            if (developmentDiscord != null) {
                sender.sendMessage(Component.text("Main Discord: ")
                        .append(Component.text(developmentDiscord).clickEvent(ClickEvent.openUrl(developmentDiscord))
                                .color(Constants.PRIMARY_COLOR)));
            }
        });
    }
}
