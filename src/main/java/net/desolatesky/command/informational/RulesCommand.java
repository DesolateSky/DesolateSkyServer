package net.desolatesky.command.informational;

import net.desolatesky.message.MessageHandler;
import net.desolatesky.message.Messages;
import net.desolatesky.player.DSPlayer;
import net.minestom.server.command.builder.Command;

public final class RulesCommand extends Command {

    private final MessageHandler messageHandler;

    public RulesCommand(MessageHandler messageHandler) {
        super("rules");
        this.messageHandler = messageHandler;

        this.setDefaultExecutor((sender, _) -> {
            if (!(sender instanceof final DSPlayer player)) {
                return;
            }
            this.messageHandler.sendMessage(player, Messages.RULES);
        });
    }

}
