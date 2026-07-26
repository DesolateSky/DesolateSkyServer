package net.desolatesky.command.informational;

import net.desolatesky.message.MessageHandler;
import net.desolatesky.message.Messages;
import net.desolatesky.player.DSPlayer;
import net.minestom.server.command.builder.Command;

public final class TutorialCommand extends Command {

    private final MessageHandler messageHandler;

    public TutorialCommand(MessageHandler messageHandler) {
        super("tutorial", "help");
        this.messageHandler = messageHandler;

        this.setDefaultExecutor((sender, _) -> {
            if (!(sender instanceof final DSPlayer player)) {
                return;
            }
            this.messageHandler.sendMessage(player, Messages.TUTORIAL);
        });
    }

}
