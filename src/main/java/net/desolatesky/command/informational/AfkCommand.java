package net.desolatesky.command.informational;

import net.desolatesky.player.DSPlayer;
import net.minestom.server.command.builder.Command;

public final class AfkCommand extends Command {

    public AfkCommand() {
        super("afk");

        this.setDefaultExecutor((sender, _) -> {
            if (!(sender instanceof final DSPlayer player)) {
                return;
            }
            player.toggleAfkStatus();
        });
    }
}
