package net.desolatesky.command.admin;

import net.desolatesky.command.console.ConsoleCommandHandler;
import net.desolatesky.logging.DSLogger;
import net.desolatesky.permission.Permission;
import net.desolatesky.player.DSPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;

import java.util.logging.Level;

public final class LogLevelCommand extends Command {

    final Argument<String> logLevelArgument = ArgumentType.String("level");


    public LogLevelCommand() {
        super("setloglevel");
        this.logLevelArgument.setSuggestionCallback((sender, context, suggestion) -> {
            suggestion.addEntry(new SuggestionEntry(Level.OFF.getName()));
            suggestion.addEntry(new SuggestionEntry(Level.SEVERE.getName()));
            suggestion.addEntry(new SuggestionEntry(Level.WARNING.getName()));
            suggestion.addEntry(new SuggestionEntry(Level.INFO.getName()));
            suggestion.addEntry(new SuggestionEntry(Level.CONFIG.getName()));
            suggestion.addEntry(new SuggestionEntry(Level.FINE.getName()));
            suggestion.addEntry(new SuggestionEntry(Level.FINER.getName()));
            suggestion.addEntry(new SuggestionEntry(Level.FINEST.getName()));
            suggestion.addEntry(new SuggestionEntry(Level.ALL.getName()));
        });

        this.setCondition((executor, _) -> {
            if (executor instanceof ConsoleCommandHandler) {
                return true;
            }
            if (!(executor instanceof final DSPlayer player)) {
                return false;
            }
            return player.hasPermission(Permission.CMD_LOG);
        });

        this.addSyntax((executor, context) -> {
            final String name = context.get(this.logLevelArgument);
            try {
                final Level level = Level.parse(name);
                DSLogger.getLogger().setLogLevel(level);
                executor.sendMessage(Component.text("Set log level to " + name + ".").color(NamedTextColor.GREEN));
            } catch (Exception ignored) {
                executor.sendMessage(Component.text(name + " is not a valid log level.").color(NamedTextColor.RED));
            }
        }, this.logLevelArgument);
    }
}
