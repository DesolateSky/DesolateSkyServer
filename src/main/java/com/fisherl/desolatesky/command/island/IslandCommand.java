package com.fisherl.desolatesky.command.island;

import com.fisherl.desolatesky.island.IslandManager;
import com.fisherl.desolatesky.message.MessageHandler;
import com.fisherl.desolatesky.message.Messages;
import com.fisherl.desolatesky.player.DSPlayer;
import com.fisherl.desolatesky.teleport.TeleportLocation;
import com.fisherl.desolatesky.teleport.TeleportManager;
import com.fisherl.desolatesky.world.WorldManager;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;

import java.util.Map;

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
    }

    private void create(CommandSender sender, CommandContext context) {
        if (!(sender instanceof final DSPlayer player)) {
            return;
        }
        this.islandManager.createIsland(player).
                thenAccept(island -> {
                    if (island == null) {
                        return;
                    }
                    this.worldManager.loadWorld(island.islandId())
                            .whenComplete((world, exception) -> {
                                if (world == null || exception != null) {
                                    this.messageHandler.sendMessage(player, Messages.ISLAND_CREATION_FAILED, Map.of("error-code", "world-missing"));
                                    return;
                                }
                                world.asInstance().loadChunk(world.getSpawnPointFor(player));
                                this.teleportManager.teleport(player, TeleportLocation.Type.ISLAND, island.islandId(), island.getSpawnPosition());
                            });
                });
    }
}
