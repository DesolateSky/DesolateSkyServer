package net.desolatesky.command.admin;

import net.desolatesky.command.console.ConsoleCommandHandler;
import net.desolatesky.data.FileDatabase;
import net.desolatesky.permission.Permission;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.player.DSPlayerData;
import net.desolatesky.server.ServerDatabases;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.inventory.TransactionOption;

import java.util.UUID;

public final class PlayerDataCommand extends Command {

    private final Argument<String> playerArgument = ArgumentType.String("player");

    private final ServerDatabases serverDatabases;
    private final FileDatabase<DSPlayerData> playerDatabase;

    public PlayerDataCommand(ServerDatabases serverDatabases, FileDatabase<DSPlayerData> playerDatabase) {
        super("playerdata");
        this.serverDatabases = serverDatabases;
        this.playerDatabase = playerDatabase;

        this.setCondition((sender, _) -> {
            if (sender instanceof ConsoleCommandHandler) {
                return true;
            }
            return sender instanceof final DSPlayer player && player.hasPermission(Permission.ADMIN);
        });

        this.addSyntax((executor, context) -> {
            final String playerName = context.get(this.playerArgument);
            final UUID uuid = this.serverDatabases.playerUUIDDatabase().getPlayerUUID(playerName).join();
            if (uuid == null) {
                executor.sendMessage(Component.text(playerName + " is not a valid player").color(NamedTextColor.RED));
                return;
            }
            final DSPlayer onlinePlayer = (DSPlayer) MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(uuid);
            final DSPlayerData playerData;
            if (onlinePlayer != null) {
                playerData = onlinePlayer.createSnapshot();
            } else {
                playerData = this.playerDatabase.loadDataNow(uuid);
            }
            if (playerData == null) {
                executor.sendMessage(Component.text(playerName + " is not a valid player").color(NamedTextColor.RED));
                return;
            }
            executor.sendMessage("Player UUID: " + uuid);
            executor.sendMessage("Island ID: " + playerData.islandId());
            if (executor instanceof final Player player) {
                final Inventory inventory = new Inventory(InventoryType.CHEST_6_ROW, Component.text(playerName + "'s Inventory"));
                inventory.addItemStacks(playerData.inventory(), TransactionOption.ALL);
                player.openInventory(inventory);
            }
        }, this.playerArgument);
    }
}
