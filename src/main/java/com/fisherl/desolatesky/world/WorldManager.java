package com.fisherl.desolatesky.world;

import com.fisherl.desolatesky.block.BlockFactory;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.SplittableRandom;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@NotNullByDefault
public final class WorldManager {

    private final BlockFactory blockFactory;

    public WorldManager(BlockFactory blockFactory) {
        this.blockFactory = blockFactory;
    }

    public CompletableFuture<DSWorld> loadWorld(UUID worldId) {
        final InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        if (instanceManager.getInstance(worldId) instanceof final DSWorld current) {
            return CompletableFuture.completedFuture(current);
        }
        final PlayerWorld world = new PlayerWorld(new SplittableRandom(), this.blockFactory, worldId, DimensionType.OVERWORLD);
        instanceManager.registerInstance(world);
        return CompletableFuture.completedFuture(world);
    }

    public CompletableFuture<DSWorld> newWorld() {
        return this.loadWorld(UUID.randomUUID());
    }
}
