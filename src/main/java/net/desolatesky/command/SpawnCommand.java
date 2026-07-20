package net.desolatesky.command;

import net.desolatesky.player.DSPlayer;
import net.desolatesky.teleport.TeleportLocation;
import net.desolatesky.teleport.TeleportManager;
import net.desolatesky.world.LobbyWorld;
import net.desolatesky.world.WorldType;
import net.minestom.server.command.builder.Command;

public final class SpawnCommand extends Command {

    public SpawnCommand(TeleportManager teleportManager) {
        super("spawn");

        this.setDefaultExecutor((sender, _) -> {
            if (!(sender instanceof final DSPlayer player)) {
                return;
            }
            teleportManager.teleport(player, new TeleportLocation(TeleportLocation.Type.SPAWN, null, LobbyWorld.ID, LobbyWorld.SPAWN, WorldType.LOBBY));
        });
    }
}
