package net.desolatesky.command.player;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.instance.DSInstanceManager;
import net.desolatesky.instance.team.TeamInstance;
import net.desolatesky.item.DSItems;
import net.desolatesky.message.MessageHandler;
import net.desolatesky.message.Messages;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.team.IslandTeam;
import net.desolatesky.team.IslandTeamManager;
import net.desolatesky.team.database.IslandCreationResult;
import net.desolatesky.team.menu.permission.PermissionMenu;
import net.desolatesky.team.role.TeamRoles;
import net.desolatesky.teleport.TeleportManager;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.entity.EntityFinder;

public final class IslandCommand extends Command {

    private final IslandTeamManager teamManager;
    private final MessageHandler messageHandler;
    private final DSInstanceManager instanceManager;
    private final TeleportManager teleportManager;

    public IslandCommand(DesolateSkyServer server) {
        super("island", "is");
        this.teamManager = server.islandTeamManager();
        this.messageHandler = server.messageHandler();
        this.instanceManager = server.instanceManager();
        this.teleportManager = server.teleportManager();

        this.setCondition((sender, _) -> sender instanceof DSPlayer);

        this.addSyntax(
                (sender, _) -> this.createIsland((DSPlayer) sender),
                ArgumentType.Literal("create")
        );

        this.addSyntax(
                (sender, _) -> this.goToIsland((DSPlayer) sender),
                ArgumentType.Literal("go")
        );

        this.addSyntax(
                (sender, _) -> this.deleteIsland((DSPlayer) sender),
                ArgumentType.Literal("delete")
        );

        this.addSyntax(
                (sender, _) -> this.permissions((DSPlayer) sender),
                ArgumentType.Literal("permissions")
        );

        final ArgumentEntity inviteTargetArgument = ArgumentType.Entity("target")
                .singleEntity(true)
                .onlyPlayers(true);
        this.addConditionalSyntax(
                (sender, _) -> ((DSPlayer) sender).hasIsland(),
                (sender, context) -> this.invite((DSPlayer) sender, context, inviteTargetArgument),
                ArgumentType.Literal("invite"),
                inviteTargetArgument
        );

        final Argument<String> islandArgument = ArgumentType.String("island");
        this.addSyntax(
                (sender, context) -> this.acceptInvite((DSPlayer) sender, context, islandArgument),
                ArgumentType.Literal("join"),
                islandArgument
        );

        this.addSyntax(
                (sender, _) -> this.leaveIsland((DSPlayer) sender),
                ArgumentType.Literal("leave")
        );
    }

    private void createIsland(DSPlayer player) {
        if (player.hasIsland()) {
            this.messageHandler.sendMessage(player, Messages.ALREADY_HAS_ISLAND);
            return;
        }
        this.teamManager.createTeam(player, player.getUsername())
                .whenComplete((result, error) -> {
                    if (error != null) {
                        this.messageHandler.sendMessage(player, Messages.ISLAND_CREATION_FAILED);
                        return;
                    }
                    if (result.type() == IslandCreationResult.Type.ON_COOLDOWN) {
                        return;
                    }
                    final IslandTeam team = result.islandTeam();
                    final TeamInstance instance = result.islandInstance();
                    if (team == null || instance == null) {
                        this.messageHandler.sendMessage(player, Messages.ISLAND_CREATION_FAILED);
                        return;
                    }
                    player.getInventory().addItemStack(DSItems.DEBRIS_CATCHER.create());
                    this.instanceManager.teleportToIsland(this.teleportManager, team, player);
                });
    }

    private void goToIsland(DSPlayer player) {
        if (!player.hasIsland()) {
            this.messageHandler.sendMessage(player, Messages.HAS_NO_ISLAND);
            return;
        }
        final IslandTeam team = this.teamManager.getTeam(player);
        if (team == null) {
            this.messageHandler.sendMessage(player, Messages.ISLAND_NOT_FOUND);
            return;
        }
        final TeamInstance instance = this.instanceManager.getIslandInstance(team, true);
        if (instance == null) {
            this.messageHandler.sendMessage(player, Messages.ISLAND_WORLD_NOT_FOUND);
            return;
        }
        this.instanceManager.teleportToIsland(this.teleportManager, team, player);
    }

    private void deleteIsland(DSPlayer player) {
        if (!player.hasIsland()) {
            this.messageHandler.sendMessage(player, Messages.HAS_NO_ISLAND);
            return;
        }
        final IslandTeam team = this.teamManager.getTeam(player);
        if (team == null) {
            this.messageHandler.sendMessage(player, Messages.ISLAND_NOT_FOUND);
            return;
        }
        this.teamManager.deleteTeam(player, team);
    }

    private void permissions(DSPlayer player) {
        if (!player.hasIsland()) {
            this.messageHandler.sendMessage(player, Messages.HAS_NO_ISLAND);
            return;
        }
        final IslandTeam team = this.teamManager.getTeam(player);
        if (team == null) {
            this.messageHandler.sendMessage(player, Messages.ISLAND_NOT_FOUND);
            return;
        }
        final PermissionMenu menu = PermissionMenu.create(this.messageHandler, team, team.teamRoles().getRole(TeamRoles.OWNER_ID));
        menu.open(player);
    }

    private void invite(DSPlayer player, CommandContext context, ArgumentEntity targetArgument) {
        if (!player.hasIsland()) {
            this.messageHandler.sendMessage(player, Messages.HAS_NO_ISLAND);
            return;
        }
        final IslandTeam team = this.teamManager.getTeam(player);
        if (team == null) {
            this.messageHandler.sendMessage(player, Messages.ISLAND_NOT_FOUND);
            return;
        }
        final EntityFinder finder = context.get(targetArgument);
        final Player targetPlayer = finder.findFirstPlayer(player);
        if (!(targetPlayer instanceof DSPlayer target)) {
            this.messageHandler.sendMessage(player, Messages.PLAYER_NOT_FOUND);
            return;
        }
        team.invite(this.messageHandler, player, target);
    }

    private void acceptInvite(DSPlayer player, CommandContext context, Argument<String> islandArgument) {
        if (player.hasIsland()) {
            this.messageHandler.sendMessage(player, Messages.ALREADY_HAS_ISLAND);
            return;
        }
        final String islandName = context.get(islandArgument);
        if (islandName == null || islandName.isEmpty()) {
            this.messageHandler.sendMessage(player, Messages.ISLAND_NOT_FOUND);
            return;
        }
        final IslandTeam team = this.teamManager.getTeamByName(islandName);
        if (team == null) {
            this.messageHandler.sendMessage(player, Messages.ISLAND_NOT_FOUND);
            return;
        }
        this.teamManager.acceptInvite(team, player);
    }

    private void leaveIsland(DSPlayer player) {
        if (!player.hasIsland()) {
            this.messageHandler.sendMessage(player, Messages.HAS_NO_ISLAND);
            return;
        }
        final IslandTeam team = this.teamManager.getTeam(player);
        if (team == null) {
            this.messageHandler.sendMessage(player, Messages.ISLAND_NOT_FOUND);
            return;
        }
        this.teamManager.leave(team, player);
    }

}
