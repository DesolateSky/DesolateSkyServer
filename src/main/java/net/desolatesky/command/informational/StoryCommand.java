package net.desolatesky.command.informational;

import net.desolatesky.message.MessageHandler;
import net.desolatesky.message.Messages;
import net.desolatesky.player.DSPlayer;
import net.minestom.server.command.builder.Command;

public final class StoryCommand extends Command {

    private final MessageHandler messageHandler;

    public StoryCommand(MessageHandler messageHandler) {
        super("story", "backgroundinfo");
        this.messageHandler = messageHandler;

        this.setDefaultExecutor((sender, _) -> {
            if (!(sender instanceof final DSPlayer player)) {
                return;
            }
            this.messageHandler.sendMessage(player, Messages.STORY);
        });
    }

}
