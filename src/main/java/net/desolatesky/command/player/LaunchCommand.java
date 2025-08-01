package net.desolatesky.command.player;

import net.desolatesky.message.MessageHandler;
import net.desolatesky.message.Messages;
import net.desolatesky.player.DSPlayer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;

public final class LaunchCommand extends Command {

    private final MessageHandler messageHandler;

    public LaunchCommand(MessageHandler messageHandler) {
        super("launch");
        this.messageHandler = messageHandler;

        this.setCondition(((sender, unused) -> {
            if (sender instanceof DSPlayer player) {
                return !player.hasIsland();
            }
            return false;
        }));

        this.setDefaultExecutor((sender, unused) -> {
            if (sender instanceof DSPlayer player) {
                this.launch(player);
            }
        });
    }

    private void launch(DSPlayer player) {
        if (player.hasIsland()) {
            this.messageHandler.sendMessage(player, Messages.ALREADY_HAS_ISLAND);
            return;
        }
        MinecraftServer.getCommandManager().execute(player, "/is create");
    }

}
