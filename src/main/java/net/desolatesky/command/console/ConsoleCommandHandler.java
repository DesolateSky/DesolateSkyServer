package net.desolatesky.command.console;

import net.desolatesky.logging.DSLogger;
import net.minestom.server.MinecraftServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public final class ConsoleCommandHandler extends Thread {

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
                MinecraftServer.getCommandManager().executeServerCommand(command);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}