package net.desolatesky.command.admin;

import net.desolatesky.command.DSCommand;
import net.desolatesky.player.DSPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity;
import net.minestom.server.command.builder.condition.CommandCondition;
import net.minestom.server.entity.Player;

public final class FlyCommand extends DSCommand {

    public static final String PERMISSION = "desolatesky.command.fly";

    public FlyCommand() {
        super(PERMISSION, "fly");

        final ArgumentEntity playerArgument = new ArgumentEntity("player")
                .onlyPlayers(true)
                .singleEntity(true);

        this.addSyntax((sender, context) -> {
            final Player targetPlayer = context.get(playerArgument).findFirstPlayer(sender);
            if (targetPlayer == null) {
                sender.sendMessage(Component.text("Player not found: " + context.get(playerArgument)).color(NamedTextColor.RED));
                return;
            }
            targetPlayer.setAllowFlying(!targetPlayer.isAllowFlying());
            targetPlayer.setFlying(targetPlayer.isAllowFlying());
        }, playerArgument);
    }

    @Override
    public CommandCondition getCondition() {
        return (sender, unused) -> sender instanceof DSPlayer;
    }

}