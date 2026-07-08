package com.fisherl.desolatesky.player.listener;

import com.fisherl.desolatesky.Listener;
import com.fisherl.desolatesky.item.ItemIds;
import com.fisherl.desolatesky.player.DSPlayer;
import com.fisherl.desolatesky.world.DSWorld;
import com.fisherl.desolatesky.world.IslandWorld;
import com.fisherl.desolatesky.world.WorldManager;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNullByDefault;

@NotNullByDefault
public class PlayerJoinListener implements Listener<PlayerEvent> {

    private final WorldManager worldManager;

    public PlayerJoinListener(WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    @Override
    public void register(EventNode<PlayerEvent> eventHandler) {
        this.registerJoinServer(eventHandler);
        this.registerJoinInstance(eventHandler);
    }

    private void registerJoinServer(EventNode<PlayerEvent> eventHandler) {
        eventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            if (!(event.getPlayer() instanceof final DSPlayer player)) {
                return;
            }
//            final DSWorld world = this.worldManager.getLobbyWorld().join();
            final DSWorld world = this.worldManager.getLobbyWorld().join();
            event.setSpawningInstance((Instance) world);
            player.setRespawnPoint(world.getSpawnPointFor(player).asPos());
        });
    }

    private void registerJoinInstance(EventNode<PlayerEvent> eventHandler) {
        eventHandler.addListener(PlayerSpawnEvent.class, event -> {
            final Player player = event.getPlayer();
            if (event.getInstance() instanceof final DSWorld world) {
                world.sendWorldBorder(player);
            }
            if (!(event.getInstance() instanceof final IslandWorld world)) {
                return;
            }
            world.itemFactory().getItemDefinition(ItemIds.ENTITY_ATTRACTOR_SILVERFISH)
                            .ifPresent(item -> player.getInventory().addItemStack(item.defaultItemStack()));
            player.setAllowFlying(true);
        });
    }
}
