package com.fisherl.desolatesky.command.admin;

import com.fisherl.desolatesky.permission.Permission;
import com.fisherl.desolatesky.player.DSPlayer;
import com.fisherl.desolatesky.server.DSServer;
import net.minestom.server.command.builder.Command;

public final class StopCommand extends Command {

    private final DSServer server;

    public StopCommand(DSServer server) {
        super("stop");
        this.server = server;
        this.setCondition((sender, _) -> {
            if (sender instanceof final DSPlayer player) {
                return player.hasPermission(Permission.CMD_STOP);
            }
            return false;
        });

        this.setDefaultExecutor((_, _) -> this.server.stop());
    }
}
