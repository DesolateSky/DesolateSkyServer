package net.desolatesky.command.console;

import net.desolatesky.logging.DSLogger;
import net.desolatesky.util.ComponentUtil;
import net.desolatesky.util.Constants;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.tag.TagHandler;
import org.jetbrains.annotations.NotNullByDefault;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@NotNullByDefault
public final class ConsoleCommandHandler extends Thread implements CommandSender {

    private static ConsoleCommandHandler instance;

    public static ConsoleCommandHandler startConsoleCommandHandler() {
        if (instance != null) {
            throw new IllegalStateException("ConsoleCommandHandler is already started.");
        }
        instance = new ConsoleCommandHandler();
        instance.start();
        return instance;
    }

    public static ConsoleCommandHandler getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ConsoleCommandHandler has not been started yet.");
        }
        return instance;
    }

    private ConsoleCommandHandler() {
        super("ConsoleCommandHandler");
        this.setDaemon(true);
    }

    @Override
    public void run() {
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (MinecraftServer.getServer().isOpen()) {
                String command = reader.readLine();
                if (command == null || command.isEmpty()) {
                    continue;
                }
                if (command.startsWith("/")) {
                    command = command.substring(1);
                }
                DSLogger.getLogger().info("Read command: %s".formatted(command));
                MinecraftServer.getCommandManager().execute(this, command);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Identity identity() {
        return Identity.identity(Constants.CONSOLE_UUID);
    }

    @Override
    public TagHandler tagHandler() {
        return TagHandler.newHandler();
    }

    @Override
    public void sendMessage(Component component) {
        DSLogger.getLogger().info(ComponentUtil.serialize(component));
    }
}