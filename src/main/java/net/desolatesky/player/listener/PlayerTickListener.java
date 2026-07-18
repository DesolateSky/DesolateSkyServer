package net.desolatesky.player.listener;

import net.desolatesky.Listener;
import net.desolatesky.island.Island;
import net.desolatesky.island.IslandManager;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.teleport.TeleportManager;
import net.desolatesky.util.Constants;
import net.desolatesky.world.IslandWorld;
import net.desolatesky.world.PlayerWorld;
import net.desolatesky.world.VoidWorld;
import net.desolatesky.world.WorldManager;
import net.desolatesky.world.WorldType;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerTickEvent;
import net.minestom.server.event.trait.PlayerEvent;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.UUID;

@NotNullByDefault
public final class PlayerTickListener implements Listener<PlayerEvent> {

    private final IslandManager islandManager;
    private final WorldManager worldManager;

    public PlayerTickListener(IslandManager islandManager, WorldManager worldManager) {
        this.islandManager = islandManager;
        this.worldManager = worldManager;
    }

    @Override
    public void register(EventNode<PlayerEvent> node) {
        node.addListener(PlayerTickEvent.class, event -> {
            if (!(event.getPlayer() instanceof final DSPlayer player)) {
                return;
            }
            if (!player.hasIsland()) {
                return;
            }
            if (player.getPosition().y() > Constants.WORLD_MIN_Y) {
                return;
            }
            if (!(player.getInstance() instanceof final IslandWorld islandWorld)) {
                return;
            }
            final WorldType worldType = islandWorld.worldType();
            final Island island = islandWorld.island();
            switch (worldType) {
                case LOBBY -> {
                    return;
                }
                case VOID, ISLAND -> {
                    final UUID voidWorldId = island.getWorldId(WorldType.VOID);
                    TeleportManager.teleportPlayerImmediate(this.worldManager, player, island.islandId(), voidWorldId, VoidWorld.SPAWN_POINT, WorldType.VOID);
                }
            }
        });
    }
}
