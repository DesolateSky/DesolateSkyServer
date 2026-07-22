package net.desolatesky.command.island;

import net.desolatesky.island.Island;
import net.desolatesky.island.IslandManager;
import net.desolatesky.logging.LoggerUtil;
import net.desolatesky.message.MessageHandler;
import net.desolatesky.message.Messages;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.teleport.TeleportLocation;
import net.desolatesky.teleport.TeleportManager;
import net.desolatesky.world.IslandWorld;
import net.desolatesky.world.WorldManager;
import net.desolatesky.world.WorldType;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Chunk;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public final class IslandCommand extends Command {

    private final TeleportManager teleportManager;
    private final MessageHandler messageHandler;
    private final WorldManager worldManager;
    private final IslandManager islandManager;

    public IslandCommand(TeleportManager teleportManager, MessageHandler messageHandler, WorldManager worldManager, IslandManager islandManager) {
        super("island", "is");
        this.teleportManager = teleportManager;
        this.messageHandler = messageHandler;
        this.worldManager = worldManager;
        this.islandManager = islandManager;

        this.addSyntax(this::create, ArgumentType.Literal("create"));
        this.addSyntax(this::go, ArgumentType.Literal("go"));
    }

    private void create(CommandSender sender, CommandContext context) {
        if (!(sender instanceof final DSPlayer player)) {
            return;
        }
        this.islandManager.createIsland(player).
                thenCompose(island -> {
                    if (island == null) {
                        return null;
                    }
                    return this.worldManager.loadWorld(island.islandId(), island.getWorldId(WorldType.ISLAND), WorldType.ISLAND);
                })
                .thenCompose(world -> {
                    if (!(world instanceof final IslandWorld islandWorld)) {
                        this.messageHandler.sendMessage(player, Messages.ISLAND_CREATION_FAILED, Map.of("error-code", "world-missing"));
                        return CompletableFuture.completedFuture(null);
                    }
                    CompletableFuture<Chunk> previous = null;
                    final Point spawn = world.getSpawnPointFor(player);
                    for (int x = -1; x <= 1; x++) {
                        for (int z = -1; z <= 1; z++) {
                            final Point chunkPos = spawn.add(x, 0, z);
                            if (previous == null) {
                                previous = world.loadChunk(chunkPos);
                            } else {
                                previous = previous.thenCompose(_ -> world.loadChunk(chunkPos));
                            }
                        }
                    }

                    world.asInstance().loadChunk(world.getSpawnPointFor(player));
                    return previous.thenApply(_ -> islandWorld.island());
                })
                .whenComplete((island, exception) -> {
                    if (exception != null) {
                        this.messageHandler.sendMessage(player, Messages.ISLAND_CREATION_FAILED, Map.of("error-code", "world-missing"));
                    }
                    this.teleportManager.teleport(player, TeleportLocation.Type.ISLAND, island.islandId(), island.getWorldId(WorldType.ISLAND), island.getSpawnPosition(), WorldType.ISLAND);
                });
    }

    private void go(CommandSender sender, CommandContext context) {
        if (!(sender instanceof final DSPlayer player)) {
            return;
        }
        final UUID islandId = player.getIslandId();
        if (islandId == null) {
            this.messageHandler.sendMessage(player, Messages.HAS_NO_ISLAND);
            return;
        }
        this.islandManager.loadOrGet(islandId).
                thenAccept(island -> {
                    if (island == null) {
                        this.messageHandler.sendMessage(player, Messages.ISLAND_LOAD_FAILED, Map.of("error-code", "island-missing"));
                        return;
                    }
                    this.worldManager.loadWorld(island.islandId(), island.getWorldId(WorldType.ISLAND), WorldType.ISLAND)
                            .whenComplete((world, exception) -> {
                                if (world == null || exception != null) {
                                    this.messageHandler.sendMessage(player, Messages.ISLAND_LOAD_FAILED, Map.of("error-code", "world-missing"));
                                    return;
                                }
                                world.asInstance().loadChunk(world.getSpawnPointFor(player));
                                this.teleportManager.teleport(player, TeleportLocation.Type.ISLAND, island.islandId(), island.getWorldId(WorldType.ISLAND), island.getSpawnPosition(), WorldType.ISLAND);
                            });
                }).whenComplete((_, error) -> {
                    if (error != null) {
                        this.messageHandler.sendMessage(player, Messages.ISLAND_LOAD_FAILED, Map.of("error-code", "exception-thrown"));
                        if (error instanceof final CompletionException completionException) {
                            LoggerUtil.logException(this.getClass(), completionException.getCause());
                        } else {
                            LoggerUtil.logException(this.getClass(), error);
                        }
                    }
                });
    }
}
