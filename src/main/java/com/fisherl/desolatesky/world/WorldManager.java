package com.fisherl.desolatesky.world;

import com.fisherl.desolatesky.block.BlockFactory;
import com.fisherl.desolatesky.entity.EntityFactory;
import com.fisherl.desolatesky.island.IslandManager;
import com.fisherl.desolatesky.item.ItemFactory;
import com.fisherl.desolatesky.lock.Lockable;
import com.fisherl.desolatesky.loot.LootFactory;
import com.fisherl.desolatesky.recipe.RecipeFactory;
import com.fisherl.desolatesky.world.lobby.LobbyWorld;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.SplittableRandom;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@NotNullByDefault
public final class WorldManager implements Lockable {

    private static final Path WORLDS_FOLDER_PATH = Path.of("worlds");

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Executor threadExecutor = Executors.newVirtualThreadPerTaskExecutor();
    private final IslandManager islandManager;
    private final BlockFactory blockFactory;
    private final ItemFactory itemFactory;
    private final EntityFactory entityFactory;
    private final LootFactory lootFactory;
    private final RecipeFactory recipeFactory;

    public WorldManager(
            IslandManager islandManager,
            BlockFactory blockFactory,
            ItemFactory itemFactory,
            EntityFactory entityFactory,
            LootFactory lootFactory,
            RecipeFactory recipeFactory
    ) {
        this.islandManager = islandManager;
        this.blockFactory = blockFactory;
        this.itemFactory = itemFactory;
        this.entityFactory = entityFactory;
        this.lootFactory = lootFactory;
        this.recipeFactory = recipeFactory;
    }

    public CompletableFuture<Boolean> worldExists(UUID worldId) {
        // todo
        return CompletableFuture.completedFuture(MinecraftServer.getInstanceManager().getInstance(worldId) != null);
    }

    public CompletableFuture<@Nullable DSWorld> loadWorld(UUID worldId) {
        final InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        if (instanceManager.getInstance(worldId) instanceof final DSWorld current) {
            return CompletableFuture.completedFuture(current);
        }
        final Path worldPath = getWorldPath(worldId);
        return this.islandManager.loadOrGet(worldId)
                .thenApplyAsync(island -> {
                    if (island == null) {
                        return null;
                    }
                    final IslandWorld world = new IslandWorld(new SplittableRandom(),
                            this.blockFactory,
                            this.itemFactory,
                            this.entityFactory,
                            this.lootFactory,
                            this.recipeFactory,
                            worldId,
                            DimensionType.OVERWORLD,
                            island,
                            worldPath);
                    instanceManager.registerInstance(world);
                    return world;
                }, this.threadExecutor);
    }

    private static Path getWorldPath(UUID worldId) {
        return WORLDS_FOLDER_PATH.resolve(worldId.toString());
    }

    public CompletableFuture<DSWorld> getLobbyWorld() {
        final InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        if (instanceManager.getInstance(LobbyWorld.ID) instanceof final DSWorld current) {
            return CompletableFuture.completedFuture(current);
        }

        return CompletableFuture.supplyAsync(() -> {
            final LobbyWorld world = new LobbyWorld(WORLDS_FOLDER_PATH,
                    this.blockFactory,
                    this.itemFactory,
                    this.entityFactory,
                    this.lootFactory,
                    this.recipeFactory
            );
            instanceManager.registerInstance(world);
            return world;
        }, this.threadExecutor);
    }

    @Override
    public ReadWriteLock lock() {
        return this.lock;
    }
}
