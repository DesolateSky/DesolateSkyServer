
package net.desolatesky.command.informational;

import net.desolatesky.message.MessageHandler;
import net.desolatesky.message.Messages;
import net.desolatesky.player.DSPlayer;
import net.minestom.server.command.builder.Command;

public final class PluginsCommand extends Command {

    public PluginsCommand(MessageHandler messageHandler) {
        super("plugins", "pl", "plugin");

        this.setDefaultExecutor((sender, _) -> {
            if (!(sender instanceof final DSPlayer player)) {
                return;
            }
            messageHandler.sendMessage(player, Messages.PLUGIN_INFO);
        });
    }
}
