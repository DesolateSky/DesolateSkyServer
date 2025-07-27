package net.desolatesky.command.admin;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.player.DSPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.ConsoleSender;
import net.minestom.server.command.ServerSender;
import net.minestom.server.command.builder.Command;

public final class StopCommand extends Command {

    public static final String PERMISSION = "desolatesky.command.stop";

    private final DesolateSkyServer server;

    public StopCommand(DesolateSkyServer server) {
        super("stop");
        this.server = server;

        this.setCondition((sender, commandString) -> {
            if (sender instanceof DSPlayer player) {
                return player.hasPermission(PERMISSION);
            }
            return sender instanceof ServerSender || sender instanceof ConsoleSender;
        });

        this.setDefaultExecutor((sender, context) -> {
            sender.sendMessage(Component.text("Stopping the server...").color(NamedTextColor.RED));
            MinecraftServer.getSchedulerManager().scheduleNextTick(this.server::stop);
        });

    }

}
