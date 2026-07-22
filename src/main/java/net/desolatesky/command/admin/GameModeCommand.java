package net.desolatesky.command.admin;

import net.desolatesky.permission.Permission;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.server.DSServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;

public final class GameModeCommand extends Command {

    private final ArgumentEnum<GameMode> gameModeArgument = ArgumentType.Enum("gamemode", GameMode.class);

    public GameModeCommand() {
        super("gamemode", "gm");
        this.setCondition((sender, _) -> {
            if (sender instanceof final DSPlayer player) {
                return player.hasPermission(Permission.CMD_GAMEMODE);
            }
            return false;
        });
        this.addSyntax((sender, context) -> {
            if (!(sender instanceof final DSPlayer player)) {
                return;
            }
            final GameMode gamemode = context.get(this.gameModeArgument);
            player.setGameMode(gamemode);
        }, this.gameModeArgument);
    }
}
