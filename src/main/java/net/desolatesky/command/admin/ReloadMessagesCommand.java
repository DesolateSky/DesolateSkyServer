package net.desolatesky.command.admin;

import net.desolatesky.command.console.ConsoleCommandHandler;
import net.desolatesky.logging.DSLogger;
import net.desolatesky.message.MessageHandler;
import net.desolatesky.permission.Permission;
import net.desolatesky.player.DSPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;

import java.io.IOException;

public final class ReloadMessagesCommand extends Command {

    public ReloadMessagesCommand(MessageHandler messageHandler) {
        super("reloadmessages");

        this.setCondition((sender, _) -> {
            if (sender instanceof ConsoleCommandHandler) {
                return true;
            }
            return sender instanceof final DSPlayer player && player.hasPermission(Permission.ADMIN);
        });

        this.setDefaultExecutor((sender, _) -> {
            try {
                messageHandler.reload();
                sender.sendMessage(Component.text("Messages reloaded!").color(NamedTextColor.GREEN));
            } catch (IOException e) {
                sender.sendMessage(Component.text("There was an error reloading the messages, check console.").color(NamedTextColor.RED));
                DSLogger.getLogger().severe(e);
            }
        });
    }
}
