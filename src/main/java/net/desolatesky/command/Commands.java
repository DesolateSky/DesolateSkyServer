package net.desolatesky.command;

import net.desolatesky.command.admin.StopCommand;
import net.desolatesky.command.player.IslandCommand;
import net.desolatesky.command.player.LaunchCommand;
import net.desolatesky.command.player.SpawnCommand;
import net.minestom.server.command.CommandManager;

public final class Commands {

    private Commands() {
    }

    public static void register(CommandManager commandManager) {
        commandManager.register(
                new StopCommand(),
                new IslandCommand(),
                new LaunchCommand(),
                new SpawnCommand()
        );
    }

}
