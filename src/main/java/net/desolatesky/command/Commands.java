package net.desolatesky.command;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.command.admin.GiveCommand;
import net.desolatesky.command.admin.StopCommand;
import net.desolatesky.command.player.CraftCommand;
import net.desolatesky.command.player.DiscordCommand;
import net.desolatesky.command.player.IslandCommand;
import net.desolatesky.command.player.LaunchCommand;
import net.desolatesky.command.player.SpawnCommand;
import net.minestom.server.command.CommandManager;

public final class Commands {

    private Commands() {
    }

    public static void register(CommandManager commandManager, DesolateSkyServer server) {
        commandManager.register(
                new StopCommand(),
                new IslandCommand(server),
                new LaunchCommand(),
                new SpawnCommand(server),
                new CraftCommand(),
                new GiveCommand(server.itemRegistry()),
                new DiscordCommand()
        );
    }

}
