package net.desolatesky.command.player;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.teleport.TeleportLocations;
import net.minestom.server.command.builder.Command;

public final class SpawnCommand extends Command {

    public SpawnCommand(DesolateSkyServer server) {
        super("spawn", "lobby");

        this.setDefaultExecutor((sender, context) -> {
            if (!(sender instanceof final DSPlayer player)) {
                return;
            }
            server.teleportManager().queue(TeleportLocations.SPAWN, player, server.instanceManager().getLobbySpawnPos());
        });
    }
}
