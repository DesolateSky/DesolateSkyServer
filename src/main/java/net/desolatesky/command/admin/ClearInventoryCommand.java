package net.desolatesky.command.admin;

import net.desolatesky.command.console.ConsoleCommandHandler;
import net.desolatesky.data.FileDatabase;
import net.desolatesky.permission.Permission;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.player.DSPlayerData;
import net.desolatesky.server.ServerDatabases;
import net.desolatesky.util.Constants;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentType;

import java.util.ArrayList;
import java.util.UUID;

public final class ClearInventoryCommand extends Command {

    private final Argument<String> playerArgument = ArgumentType.String("player");

    private final ServerDatabases serverDatabases;
    private final FileDatabase<DSPlayerData> playerDatabase;

    public ClearInventoryCommand(ServerDatabases serverDatabases, FileDatabase<DSPlayerData> playerDatabase) {
        super("clearinventory");
        this.serverDatabases = serverDatabases;
        this.playerDatabase = playerDatabase;

        this.setCondition((sender, _) -> {
            if (sender instanceof ConsoleCommandHandler) {
                return true;
            }
            return sender instanceof final DSPlayer player && player.hasPermission(Permission.ADMIN);
        });

        this.addSyntax((sender, context) -> {
            final String playerName = context.get(this.playerArgument);
            final UUID uuid = this.serverDatabases.playerUUIDDatabase().getPlayerUUID(playerName).join();
            if (uuid == null) {
                sender.sendMessage(Component.text(playerName + " is not a valid player").color(NamedTextColor.RED));
                return;
            }
            final DSPlayer onlinePlayer = (DSPlayer) MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(uuid);
            final DSPlayerData playerData;
            if (onlinePlayer != null) {
                onlinePlayer.getInventory().clear();
                sender.sendMessage(Component.text("Cleared " + playerName + "'s inventory.").color(NamedTextColor.RED));
                if (sender instanceof final DSPlayer player) {
                    onlinePlayer.sendMessage(Component.text("Your inventory was cleared by " + player.getUsername()));
                } else {
                    onlinePlayer.sendMessage(Component.text("Your inventory was cleared by " + Constants.CONSOLE_NAME));
                }
                return;
            } else {
                playerData = this.playerDatabase.loadDataNow(uuid);
            }
            if (playerData == null) {
                sender.sendMessage(Component.text(playerName + " is not a valid player").color(NamedTextColor.RED));
                return;
            }
            final DSPlayerData newPlayerData = new DSPlayerData(playerData.uuid(), playerData.islandId(), new ArrayList<>(), playerData.logoutPos());
            this.playerDatabase.saveData(newPlayerData.uuid(), newPlayerData);
            sender.sendMessage(Component.text("Cleared " + playerName + "'s (offline) inventory.").color(NamedTextColor.RED));
        }, this.playerArgument);
    }
}
