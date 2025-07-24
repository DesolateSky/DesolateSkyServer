package net.desolatesky.command;

import net.desolatesky.player.DSPlayer;
import net.minestom.server.command.ConsoleSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.condition.CommandCondition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class DSCommand extends Command {

    public DSCommand(@Nullable String permission, @NotNull String name, @Nullable String... aliases) {
        super(name, aliases);
        if (permission != null) {
            this.setCondition(this.createCommandCondition(permission));
        }
    }

    public DSCommand(@Nullable String permission, @NotNull String name) {
        super(name);
        if (permission != null) {
            this.setCondition(this.createCommandCondition(permission));
        }
    }

    protected final CommandCondition getCommandCondition() {
        return null;
    }

    private CommandCondition createCommandCondition(String permission) {
        final CommandCondition condition = this.getCommandCondition();
        if (condition == null) {
            return (sender, command) -> {
                if (sender instanceof ConsoleSender) {
                    return true;
                }
                return sender instanceof final DSPlayer player && player.hasPermission(permission);
            };
        }
        return (sender, command) -> {
            if (sender instanceof ConsoleSender) {
                return true;
            }
            if (sender instanceof final DSPlayer player) {
                return player.hasPermission(permission) && condition.canUse(sender, command);
            }
            return false;
        };
    }

}
