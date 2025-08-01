package net.desolatesky.command.admin;

import net.desolatesky.player.DSPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;

public final class ClearInventoryCommand extends Command {

    public static final String PERMISSION = "desolatesky.command.clearinventory";

    public ClearInventoryCommand() {
        super("clearinventory");

        this.setCondition((sender, unused) -> sender instanceof DSPlayer player && player.hasPermission(PERMISSION));

        this.setDefaultExecutor((sender, unused) -> {
            final DSPlayer player = (DSPlayer) sender;
            player.getInventory().clear();
            sender.sendMessage(Component.text("Cleared your inventory...").color(NamedTextColor.RED));
        });

    }

}