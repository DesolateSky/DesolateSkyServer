package com.fisherl.desolatesky.player.listener;

import com.fisherl.desolatesky.Listener;
import com.fisherl.desolatesky.world.DSWorld;
import com.fisherl.desolatesky.world.PlayerWorld;
import com.fisherl.desolatesky.world.WorldManager;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
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
            final Player player = event.getPlayer();
            final DSWorld world = this.worldManager.loadWorld(player.getUuid()).join();
            event.setSpawningInstance((Instance) world);
            player.setGameMode(GameMode.SURVIVAL);
            player.setRespawnPoint(PlayerWorld.DEFAULT_SPAWN_POINT.asPos());
        });
    }

    private void registerJoinInstance(EventNode<PlayerEvent> eventHandler) {
        eventHandler.addListener(PlayerSpawnEvent.class, event -> {
            final Player player = event.getPlayer();
            player.getInventory().addItemStack(ItemStack.of(Material.DIRT, 64));
            player.setAllowFlying(true);
        });
    }
}
