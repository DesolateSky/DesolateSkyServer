package net.desolatesky.command.admin;

import net.desolatesky.command.console.ConsoleCommandHandler;
import net.desolatesky.permission.Permission;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.player.whitelist.PlayerWhitelist;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity;
import net.minestom.server.entity.Entity;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@NotNullByDefault
public final class WhitelistCommand extends Command {

    private final PlayerWhitelist playerWhitelist;
    private final ArgumentEntity playerArg = ArgumentType.Entity("player").onlyPlayers(true).singleEntity(true);
    private final ArgumentString playerUUIDArg = ArgumentType.String("uuid");

    public WhitelistCommand(PlayerWhitelist playerWhitelist) {
        super("whitelist");
        this.playerWhitelist = playerWhitelist;

        this.setCondition(this::hasPermission);

        this.setDefaultExecutor(this::onList);

        this.addSyntax(this::onAdd, this.playerArg);
        this.addSyntax(this::onAdd, this.playerUUIDArg);
        this.addSyntax(this::onRemove, this.playerArg);
        this.addSyntax(this::onRemove, this.playerUUIDArg);
    }

    private boolean hasPermission(@Nullable CommandSender sender, @Nullable String unused) {
        return sender instanceof ConsoleCommandHandler || (sender instanceof final DSPlayer player && player.hasPermission(Permission.CMD_WHITELIST));
    }

    private void onAdd(CommandSender sender, CommandContext context) {
        this.onModify(sender, context, true);
    }

    private void onRemove(CommandSender sender, CommandContext context) {
        this.onModify(sender, context, false);
    }

    private void onModify(CommandSender sender, CommandContext context, boolean add) {
        final UUID playerId;
        if (context.has(this.playerArg)) {
            final Entity entity = context.get(this.playerArg).findFirstEntity(null, null);
            if (entity == null) {
                throw new IllegalArgumentException("Entity not found");
            }
            playerId = entity.getUuid();
        } else if (context.has(this.playerUUIDArg)) {
            playerId = UUID.fromString(context.get(this.playerUUIDArg));
        } else {
            throw new IllegalArgumentException("Invalid command");
        }
        if (add) {
            final boolean added = this.playerWhitelist.addPlayer(playerId);
            if (added) {
                sender.sendMessage(Component.text("Player added to the whitelist.").color(NamedTextColor.GREEN));
            } else {
                sender.sendMessage(Component.text("Player already in the whitelist!").color(NamedTextColor.RED));
            }
        } else {
            final boolean removed = this.playerWhitelist.removePlayer(playerId);
            if (removed) {
                sender.sendMessage(Component.text("Player remove from the whitelist.").color(NamedTextColor.GREEN));
            } else {
                sender.sendMessage(Component.text("Player not in the whitelist!").color(NamedTextColor.RED));
            }
        }
    }

    private void onList(CommandSender sender, CommandContext context) {
        sender.sendMessage(String.join(", ", this.playerWhitelist.getWhitelistedPlayers()));
    }
}
