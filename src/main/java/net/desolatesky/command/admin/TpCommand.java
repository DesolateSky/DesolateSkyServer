package net.desolatesky.command.admin;

import net.desolatesky.permission.Permission;
import net.desolatesky.player.DSPlayer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;

public final class TpCommand extends Command {

    private final ArgumentEntity playerArgument = ArgumentType.Entity("player")
            .onlyPlayers(true)
            .singleEntity(true);

    public TpCommand() {
        super("teleport", "tp");
        this.setCondition((sender, _) -> {
            if (sender instanceof final DSPlayer player) {
                return player.hasPermission(Permission.CMD_TELEPORT);
            }
            return false;
        });
        this.addSyntax((sender, context) -> {
            if (!(sender instanceof final DSPlayer player)) {
                return;
            }
            final Player other = (Player) context.get(this.playerArgument).findFirstEntity(null, null);
            if (other == null) {
                return;
            }
            if (!other.getInstance().equals(player.getInstance())) {
                player.setInstance(other.getInstance());
            }
            player.teleport(other.getPosition());
        }, this.playerArgument);
    }
}
