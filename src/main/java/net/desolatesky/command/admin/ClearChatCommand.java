package net.desolatesky.command.admin;

import net.desolatesky.command.console.ConsoleCommandHandler;
import net.desolatesky.permission.Permission;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.Constants;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.network.packet.server.configuration.ResetChatPacket;

public final class ClearChatCommand extends Command {

    public ClearChatCommand() {
        super("clearchat");
        this.setCondition((sender, _) -> {
            if (sender instanceof ConsoleCommandHandler) {
                return true;
            }
            return sender instanceof final DSPlayer player && player.hasPermission(Permission.CMD_CLEAR_CHAT);
        });

        this.setDefaultExecutor((sender, _) -> {
            final Component senderName;
            if (sender instanceof final DSPlayer player) {
                senderName = player.getDisplayName();
            } else {
                senderName = Component.text(Constants.CONSOLE_NAME);
            }
            MinecraftServer.getConnectionManager().getOnlinePlayers().forEach(p -> {
                if (p instanceof DSPlayer player && player.hasPermission(Permission.CMD_CLEAR_CHAT)) {
                    return;
                }
                for (int i = 0; i < 100; i++) {
                    p.sendMessage(Component.empty());
                }
            });
            MinecraftServer.getConnectionManager().getOnlinePlayers().forEach(p -> {
                p.sendMessage(Component.text("Chat was cleared by ").color(NamedTextColor.RED).append(senderName));
                if (p instanceof DSPlayer player && player.hasPermission(Permission.CMD_CLEAR_CHAT)) {
                    p.sendMessage(Component.text("Your chat was not cleared because you have administrator permissions."));
                }
            });
        });
    }
}
