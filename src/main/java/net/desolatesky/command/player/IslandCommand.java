package net.desolatesky.command.player;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.instance.DSInstanceManager;
import net.desolatesky.instance.team.TeamInstance;
import net.desolatesky.message.Messages;
import net.desolatesky.player.DSPlayer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;

public final class IslandCommand extends Command {

    public IslandCommand() {
        super("island", "is");

        this.setCondition((sender, command) -> {
            if (!(sender instanceof final DSPlayer player)) {
                return false;
            }
            if (command == null) {
                return true;
            }
            final String[] args = command.split(" ");
            if (args.length < 2) {
                return true;
            }
            if (args[1].equals("created")) {
                return !player.hasIsland();
            }
            return true;
        });

        this.addSyntax((sender, context) -> {
            this.createIsland((DSPlayer) sender);
        }, ArgumentType.Literal("create"));

        this.addSyntax((sender, context) -> {
            this.goToIsland((DSPlayer) sender);
        }, ArgumentType.Literal("go"));
    }

    private void createIsland(DSPlayer player) {
        if (player.hasIsland()) {
            player.sendIdMessage(Messages.ALREADY_HAS_ISLAND);
            return;
        }
        final DSInstanceManager instanceManager = DesolateSkyServer.get().instanceManager();
        instanceManager.createIslandInstance(player);
        instanceManager.teleportToIsland(player);
    }

    private void goToIsland(DSPlayer player) {
        if (!player.hasIsland()) {
            player.sendMessage(Messages.HAS_NO_ISLAND);
            return;
        }
        final DSInstanceManager instanceManager = DesolateSkyServer.get().instanceManager();
        final TeamInstance instance = instanceManager.getPlayerIsland(player, true);
        if (instance == null) {
            player.sendMessage(Messages.ISLAND_NOT_FOUND);
            return;
        }
        instanceManager.teleportToIsland(player);
    }

}
