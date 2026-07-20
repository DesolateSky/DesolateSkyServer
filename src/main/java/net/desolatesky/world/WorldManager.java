package net.desolatesky.world;

import net.desolatesky.block.BlockFactory;
import net.desolatesky.entity.EntityFactory;
import net.desolatesky.island.IslandManager;
import net.desolatesky.item.ItemFactory;
import net.desolatesky.lock.Lockable;
import net.desolatesky.loot.LootFactory;
import net.desolatesky.recipe.RecipeFactory;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

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

    public CompletableFuture<@Nullable DSWorld> loadWorld(@UnknownNullability UUID islandId, UUID worldId, WorldType worldType) {
        if (worldId.equals(LobbyWorld.ID)) {
            return this.getLobbyWorld();
        }
        final InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        if (instanceManager.getInstance(worldId) instanceof final DSWorld current) {
            return CompletableFuture.completedFuture(current);
        }
        if (islandId == null) {
            return CompletableFuture.completedFuture(null);
        }
        final Path worldPath = getWorldPath(worldId);
        return this.islandManager.loadOrGet(islandId)
                .thenApplyAsync(island -> {
                    if (island == null) {
                        return null;
                    }
                    final DSWorld world = switch (worldType) {
                        case LOBBY -> this.getLobbyWorld().join();
                        case VOID -> new VoidWorld(new SplittableRandom(),
                                this.blockFactory,
                                this.itemFactory,
                                this.entityFactory,
                                this.lootFactory,
                                this.recipeFactory,
                                worldPath,
                                island);
                        case ISLAND -> new PlayerWorld(new SplittableRandom(),
                                this.blockFactory,
                                this.itemFactory,
                                this.entityFactory,
                                this.lootFactory,
                                this.recipeFactory,
                                DimensionType.OVERWORLD,
                                worldPath,
                                island);
                    };
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
            final LobbyWorld world = new LobbyWorld(
                    new SplittableRandom(),
                    this.blockFactory,
                    this.itemFactory,
                    this.entityFactory,
                    this.lootFactory,
                    this.recipeFactory,
                    DimensionType.OVERWORLD,
                    WORLDS_FOLDER_PATH
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
