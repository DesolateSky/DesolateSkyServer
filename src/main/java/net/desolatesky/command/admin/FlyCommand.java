package net.desolatesky.command.admin;

import net.desolatesky.permission.Permission;
import net.desolatesky.player.DSPlayer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;

public final class FlyCommand extends Command {

    public FlyCommand() {
        super("fly");
        this.setCondition((sender, _) -> {
            if (sender instanceof final DSPlayer player) {
                return player.hasPermission(Permission.CMD_FLY);
            }
            return false;
        });
        this.setDefaultExecutor((sender, _) -> {
            if (!(sender instanceof final DSPlayer player)) {
                return;
            }
            final boolean flying = !player.isFlying();
            player.setAllowFlying(flying);
            player.setFlying(flying);
        });
    }
}
