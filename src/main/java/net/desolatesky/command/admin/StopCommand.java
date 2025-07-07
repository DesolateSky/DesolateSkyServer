package net.desolatesky.command.admin;

import net.desolatesky.player.DSPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.ConsoleSender;
import net.minestom.server.command.ServerSender;
import net.minestom.server.command.builder.Command;

public final class StopCommand extends Command {

    public static final String permission = "desolatesky.command.stop";

    public StopCommand() {
        super("stop");

        this.setCondition((sender, commandString) -> {
            if (sender instanceof DSPlayer player) {
                return player.hasPermission(permission);
            }
            return sender instanceof ServerSender || sender instanceof ConsoleSender;
        });

        this.setDefaultExecutor((sender, context) -> {
            sender.sendMessage(Component.text("Stopping the server...").color(NamedTextColor.RED));
            MinecraftServer.getSchedulerManager().scheduleNextTick(MinecraftServer::stopCleanly);
        });

    }

}
