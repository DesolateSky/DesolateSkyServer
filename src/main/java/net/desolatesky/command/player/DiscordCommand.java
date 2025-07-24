package net.desolatesky.command.player;

import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.Constants;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.minestom.server.command.builder.Command;

public final class DiscordCommand extends Command {

    public DiscordCommand() {
        super("discord");

        this.setDefaultExecutor((sender, context) -> {
            if (!(sender instanceof final DSPlayer player)) {
                return;
            }
            player.sendMessage(Component.text("Join our discord: https://discord.gg/p8RWreq8Dz").color(Constants.ACCENT_COLOR).clickEvent(ClickEvent.openUrl("https://discord.gg/p8RWreq8Dz")));
        });
    }

}
