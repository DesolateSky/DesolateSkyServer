package net.desolatesky.player.listener;

import net.desolatesky.Listener;
import net.desolatesky.data.FileDatabase;
import net.desolatesky.island.Island;
import net.desolatesky.island.IslandManager;
import net.desolatesky.island.IslandSnapshot;
import net.desolatesky.item.ItemFactory;
import net.desolatesky.item.ItemIds;
import net.desolatesky.item.definition.ItemDefinition;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.player.DSPlayerData;
import net.desolatesky.util.InventoryUtil;
import net.desolatesky.world.DSWorld;
import net.desolatesky.world.IslandWorld;
import net.desolatesky.world.WorldManager;
import net.desolatesky.world.pos.WorldPosition;
import net.minestom.server.coordinate.Point;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerLoadedEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.UUID;

@NotNullByDefault
public class PlayerJoinListener implements Listener<PlayerEvent> {

    private final FileDatabase<DSPlayerData> playerDatabase;
    private final FileDatabase<IslandSnapshot> islandDatabase;
    private final WorldManager worldManager;
    private final IslandManager islandManager;
    private final ItemFactory itemFactory;

    public PlayerJoinListener(
            FileDatabase<DSPlayerData> playerDatabase,
            FileDatabase<IslandSnapshot> islandDatabase,
            WorldManager worldManager,
            IslandManager islandManager,
            ItemFactory itemFactory
    ) {
        this.playerDatabase = playerDatabase;
        this.islandDatabase = islandDatabase;
        this.worldManager = worldManager;
        this.islandManager = islandManager;
        this.itemFactory = itemFactory;
    }

    @Override
    public void register(EventNode<PlayerEvent> eventHandler) {
        this.registerJoinServer(eventHandler);
        this.registerJoinInstance(eventHandler);
        this.registerPlayerLoad(eventHandler);
        this.registerDisconnect(eventHandler);
    }

    private void registerJoinServer(EventNode<PlayerEvent> eventHandler) {
        eventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            if (!(event.getPlayer() instanceof final DSPlayer player)) {
                return;
            }
            final WorldPosition logoutPos = player.getLogoutPos();
            DSWorld world = null;
            if (logoutPos != null) {
                world = this.worldManager.loadWorld(logoutPos.islandId(), logoutPos.worldId(), logoutPos.worldType()).join();
                player.setRespawnPoint(logoutPos.pos().asPos());
            }
            if (world == null) {
                world = this.worldManager.getLobbyWorld().join();
                player.setRespawnPoint(world.getSpawnPointFor(player).asPos());
            }
            player.setLogoutPos(null);
            event.setSpawningInstance(world);
            if (player.newPlayer()) {
                final ItemDefinition itemDefinition = this.itemFactory.getItemDefinition(ItemIds.STARTING_CACHE);
                if (itemDefinition != null) {
                    InventoryUtil.addItemToInventory(player, itemDefinition.defaultItemStack(), player.getInstance(), player.getPosition());
                }
            }
        });
    }

    private void registerJoinInstance(EventNode<PlayerEvent> eventHandler) {
        eventHandler.addListener(PlayerSpawnEvent.class, event -> {
            final DSPlayer player = (DSPlayer) event.getPlayer();
            if (event.getInstance() instanceof final DSWorld world) {
                world.sendWorldBorder(player);
            }
            this.playerDatabase.saveData(player.getUuid(), player.createSnapshot());
            if (!(event.getInstance() instanceof final IslandWorld world)) {
                return;
            }
            this.islandDatabase.saveData(world.island().islandId(), world.island().createSnapshot());
            player.setAllowFlying(true);
        });
    }

    private void registerPlayerLoad(EventNode<PlayerEvent> node) {
        node.addListener(PlayerLoadedEvent.class, event -> {
            final DSPlayer player = (DSPlayer) event.getPlayer();
            final UUID islandId = player.getIslandId();
            if (!(event.getInstance() instanceof final DSWorld world)) {
                return;
            }
            if (islandId != null) {
                this.islandManager.loadOrGet(islandId).thenAccept(island -> {
                    if (island != null) {
                        island.onMemberJoin(player, world);
                    }
                });
            }
        });
    }

    private void registerDisconnect(EventNode<PlayerEvent> eventHandler) {
        eventHandler.addListener(PlayerDisconnectEvent.class, event -> {
            final DSPlayer player = (DSPlayer) event.getPlayer();
            player.setLogoutPos(player.getWorldPosition());
            this.playerDatabase.saveData(player.getUuid(), player.createSnapshot());
            final UUID islandID = player.getIslandId();
            if (islandID == null) {
                return;
            }
            final Island island = this.islandManager.getLoaded(islandID);
            if (island == null) {
                return;
            }
            if (event.getInstance() instanceof final DSWorld world) {
                island.onMemberLeave(player, world);
            }
            this.islandDatabase.saveData(islandID, island.createSnapshot());
        });
    }
}
