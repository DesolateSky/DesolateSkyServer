package net.desolatesky.command.player;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.instance.DSInstanceManager;
import net.desolatesky.instance.team.TeamInstance;
import net.desolatesky.message.Messages;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.teleport.TeleportManager;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.instance.InstanceManager;

public final class IslandCommand extends Command {

    private final DSInstanceManager instanceManager;
    private final TeleportManager teleportManager;

    public IslandCommand(DesolateSkyServer server) {
        super("island", "is");
        this.instanceManager = server.instanceManager();
        this.teleportManager = server.teleportManager();

        this.setCondition((sender, command) -> sender instanceof DSPlayer);

        this.addConditionalSyntax(
                (sender, context) -> !((DSPlayer) sender).hasIsland(),
                (sender, context) -> this.createIsland((DSPlayer) sender),
                ArgumentType.Literal("create")
        );

        this.addConditionalSyntax(
                (sender, context) -> ((DSPlayer) sender).hasIsland(),
                (sender, context) -> this.goToIsland((DSPlayer) sender),
                ArgumentType.Literal("go")
        );
    }

    private void createIsland(DSPlayer player) {
        if (player.hasIsland()) {
            player.sendIdMessage(Messages.ALREADY_HAS_ISLAND);
            return;
        }
        this.instanceManager.createIslandInstance(player);
        this.instanceManager.teleportToIsland(this.teleportManager, player);
    }

    private void goToIsland(DSPlayer player) {
        if (!player.hasIsland()) {
            player.sendMessage(Messages.HAS_NO_ISLAND);
            return;
        }
        final TeamInstance instance = this.instanceManager.getPlayerIsland(player, true);
        if (instance == null) {
            player.sendMessage(Messages.ISLAND_NOT_FOUND);
            return;
        }
        this.instanceManager.teleportToIsland(this.teleportManager, player);
    }

}
