package net.desolatesky.command.admin;

import net.desolatesky.command.console.ConsoleCommandHandler;
import net.desolatesky.config.ConfigFile;
import net.desolatesky.permission.Permission;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.server.ServerDatabases;
import net.desolatesky.util.Constants;
import net.desolatesky.util.DateTimeUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentLiteral;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Player;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public final class BanCommand extends Command {

    private final ArgumentLiteral banCommand = ArgumentType.Literal("ban");
    private final ArgumentLiteral unbanCommand = ArgumentType.Literal("unban");

    private final Argument<String> playerArgument = ArgumentType.String("player");
    final Argument<Long> daysArgument = ArgumentType.Long("days").between(0L, Long.MAX_VALUE);
    final Argument<Long> hoursArgument = ArgumentType.Long("hours").between(0L, Long.MAX_VALUE);
    final Argument<Long> minutesArgument = ArgumentType.Long("minutes").between(0L, Long.MAX_VALUE);
    final Argument<Long> secondsArgument = ArgumentType.Long("seconds").between(0L, Long.MAX_VALUE);
    final Argument<String[]> reasonArgument = ArgumentType.StringArray("reason");

    private final ConfigFile serverConfig;
    private final ServerDatabases serverDatabases;

    public BanCommand(ConfigFile serverConfig, ServerDatabases serverDatabases) {
        super("ban");
        this.serverConfig = serverConfig;
        this.serverDatabases = serverDatabases;

        this.playerArgument.setSuggestionCallback((sender, context, suggestion) -> {
            MinecraftServer.getConnectionManager().getOnlinePlayers().forEach(p -> {
                if (sender.equals(p)) {
                    return;
                }
                suggestion.addEntry(new SuggestionEntry(p.getUsername()));
            });
        });

        this.setCondition((executor, _) -> {
            if (executor instanceof ConsoleCommandHandler) {
                return true;
            }
            if (!(executor instanceof final DSPlayer player)) {
                return false;
            }
            return player.hasPermission(Permission.CMD_BAN);
        });

        this.addSyntax((executor, context) -> {
            final String name = context.get(this.playerArgument);
            final UUID playerId = this.serverDatabases.playerUUIDDatabase().getPlayerUUID(name).join();
            if (playerId == null) {
                executor.sendMessage(name + " was not found!");
                return;
            }
            final long days = context.get(this.daysArgument);
            final long hours = context.get(this.hoursArgument);
            final long minutes = context.get(this.minutesArgument);
            final long seconds = context.get(this.secondsArgument);
            final String[] reasonArray = context.get(this.reasonArgument);
            final String reason = String.join(" ", reasonArray);

            Duration duration;
            try {
                duration = Duration.ofDays(days)
                        .plusHours(hours)
                        .plusMinutes(minutes)
                        .plusSeconds(seconds);
            } catch (ArithmeticException e) {
                duration = Duration.between(Instant.now(), Instant.MAX);
            }
            final Instant expiration = Instant.now().plus(duration);
            final UUID bannerId;
            final String bannerName;
            if (executor instanceof final Player player) {
                bannerId = player.getUuid();
                bannerName = player.getUsername();
            } else {
                bannerId = Constants.UUID_ZERO;
                bannerName = Constants.CONSOLE_NAME;
            }
            final Duration finalDuration = duration;
            this.serverDatabases.banDatabase().saveBan(playerId, expiration, reason, bannerId)
                    .thenAccept(ban -> {
                        executor.sendMessage("You banned " + name + " for " + DateTimeUtil.durationToString(finalDuration) + ".");
                        final Player onlinePlayer = MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(playerId);
                        if (onlinePlayer == null) {
                            return;
                        }
                        ban.kick(onlinePlayer, bannerName, this.serverConfig.rootNode().node("discord").getString(""));
                    });
        }, this.banCommand, this.playerArgument, this.daysArgument, this.hoursArgument, this.minutesArgument, this.secondsArgument, this.reasonArgument);

        this.addSyntax((executor, context) -> {
            final String name = context.get(this.playerArgument);
            final UUID playerId = this.serverDatabases.playerUUIDDatabase().getPlayerUUID(name).join();
            if (playerId == null) {
                executor.sendMessage(Component.text(name + " is not a valid player!"));
                return;
            }
            final UUID bannerId;
            if (executor instanceof final Player player) {
                bannerId = player.getUuid();
            } else {
                bannerId = Constants.UUID_ZERO;
            }
            this.serverDatabases.banDatabase().unbanPlayer(playerId, bannerId)
                    .thenAccept(result -> {
                        if (!result) {
                            executor.sendMessage(Component.text(name + " is not a banned player!").color(NamedTextColor.RED));
                        } else {
                            executor.sendMessage(Component.text(name + " was unbanned!").color(NamedTextColor.GREEN));
                        }
                    });
        }, this.unbanCommand, this.playerArgument);
    }
}
