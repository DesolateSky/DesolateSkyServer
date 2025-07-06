package net.desolatesky.command.player;

import net.desolatesky.message.Messages;
import net.desolatesky.player.DSPlayer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;

public final class LaunchCommand extends Command {

    public LaunchCommand() {
        super("launch");

        this.setCondition(((sender, commandString) -> {
            if (sender instanceof DSPlayer player) {
                return !player.hasIsland();
            }
            return false;
        }));

        this.setDefaultExecutor((sender, context) -> {
            if (sender instanceof DSPlayer player) {
                this.launch(player);
            }
        });
    }

    private void launch(DSPlayer player) {
        if (player.hasIsland()) {
            player.sendIdMessage(Messages.ALREADY_HAS_ISLAND);
            return;
        }
        MinecraftServer.getCommandManager().execute(player, "/is create");
    }

}
