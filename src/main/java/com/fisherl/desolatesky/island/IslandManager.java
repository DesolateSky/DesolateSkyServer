package com.fisherl.desolatesky.island;

import com.fisherl.desolatesky.island.role.IslandRole;
import com.fisherl.desolatesky.lock.Lockable;
import com.fisherl.desolatesky.message.MessageHandler;
import com.fisherl.desolatesky.message.Messages;
import com.fisherl.desolatesky.player.DSPlayer;
import com.fisherl.desolatesky.world.IslandWorld;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class IslandManager implements Lockable {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final MessageHandler messageHandler;
    private final Map<UUID, Island> islands = new HashMap<>();

    public IslandManager(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public CompletableFuture<@Nullable Island> loadOrGet(UUID islandId) {
        return this.lockRead(() -> {
            final Island loaded = this.islands.get(islandId);
            if (loaded != null) {
                return CompletableFuture.completedFuture(loaded);
            }
            return CompletableFuture.completedFuture(null);
        });
    }

    public CompletableFuture<@Nullable Island> createIsland(DSPlayer player) {
        final boolean allowed = this.lockWrite(() -> {
            if (player.isCreatingIsland()) {
                return false;
            }
            player.setCreatingIsland(true);
            return true;
        });
        if (!allowed || player.hasIsland()) {
            this.messageHandler.sendMessage(player, Messages.ALREADY_HAS_ISLAND);
            return CompletableFuture.completedFuture(null);
        }
        final UUID islandId = UUID.randomUUID();
        return CompletableFuture.supplyAsync(() -> this.lockWrite(() -> {
                    final Map<UUID, IslandRole> islandRoles = new HashMap<>();
                    islandRoles.put(player.getUuid(), IslandRole.OWNER);
                    final DSIsland island = new DSIsland(islandId, islandRoles, new HashMap<>(), player.getName().append(Component.text("'s Island")), IslandWorld.STARTING_REGION);
                    this.islands.put(islandId, island);
                    player.setIslandId(islandId);
                    player.setCreatingIsland(false);
                    return island;
                }))
                .thenApply(island -> {
                    if (island == null) {
                        this.messageHandler.sendMessage(player, Messages.ISLAND_CREATION_FAILED, Map.of("error-code", 0));
                        return null;
                    }
                    this.messageHandler.sendMessage(player, Messages.CREATED_ISLAND);
                    return island;
                });
    }

    @Override
    public ReadWriteLock lock() {
        return this.lock;
    }
}
