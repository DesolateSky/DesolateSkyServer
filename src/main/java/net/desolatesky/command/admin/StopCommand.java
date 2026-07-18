package net.desolatesky.command.admin;

import net.desolatesky.permission.Permission;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.server.DSServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;

public final class StopCommand extends Command {

    private final DSServer server;

    public StopCommand(DSServer server) {
        super("stop");
        this.server = server;
        this.setCondition((sender, _) -> {
            if (sender instanceof final DSPlayer player) {
                return player.hasPermission(Permission.CMD_STOP);
            }
            return !(sender instanceof Player);
        });

        this.setDefaultExecutor((_, _) -> this.server.stop());
    }
}
