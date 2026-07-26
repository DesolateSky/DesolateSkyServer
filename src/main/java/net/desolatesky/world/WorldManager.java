package net.desolatesky.world;

import net.desolatesky.block.BlockFactory;
import net.desolatesky.entity.EntityManager;
import net.desolatesky.island.IslandManager;
import net.desolatesky.item.ItemFactory;
import net.desolatesky.lock.Lockable;
import net.desolatesky.logging.DSLogger;
import net.desolatesky.loot.LootFactory;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.recipe.RecipeFactory;
import net.desolatesky.util.FileUtil;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
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
    private final EntityManager entityFactory;
    private final LootFactory lootFactory;
    private final RecipeFactory recipeFactory;

    public WorldManager(
            IslandManager islandManager,
            BlockFactory blockFactory,
            ItemFactory itemFactory,
            EntityManager entityFactory,
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

    public CompletableFuture<@Nullable DSWorld> loadWorld(@UnknownNullability UUID islandId, WorldType worldType) {
        if (worldType == WorldType.LOBBY) {
            return this.getLobbyWorld();
        }
        final InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        if (islandId == null) {
            return CompletableFuture.completedFuture(null);
        }
        return this.islandManager.loadOrGet(islandId)
                .thenApplyAsync(island -> {
                    if (island == null) {
                        return null;
                    }
                    final UUID worldId = island.getWorldId(worldType);
                    if (instanceManager.getInstance(worldId) instanceof final DSWorld current) {
                        return current;
                    }
                    final DSWorld world = switch (worldType) {
                        case VOID -> new VoidWorld(new SplittableRandom(),
                                this.blockFactory,
                                this.itemFactory,
                                this.entityFactory,
                                this.lootFactory,
                                this.recipeFactory,
                                WORLDS_FOLDER_PATH.resolve(island.islandId().toString()),
                                island);
                        case ISLAND -> new PlayerWorld(new SplittableRandom(),
                                this.blockFactory,
                                this.itemFactory,
                                this.entityFactory,
                                this.lootFactory,
                                this.recipeFactory,
                                DimensionType.OVERWORLD,
                                WORLDS_FOLDER_PATH.resolve(island.islandId().toString()),
                                island);
                        default -> throw new IllegalStateException("Unexpected value: " + worldType);
                    };
                    instanceManager.registerInstance(world);
                    return world;
                }, this.threadExecutor);
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

    public void unloadWorld(UUID worldId) {
        final Instance instance = MinecraftServer.getInstanceManager().getInstance(worldId);
        if (instance == null) {
            return;
        }
        if (!(instance instanceof final DSWorld world)) {
            MinecraftServer.getInstanceManager().unregisterInstance(instance);
            return;
        }
        world.save();
        MinecraftServer.getInstanceManager().unregisterInstance(world);
    }

    public void deleteWorld(UUID worldId) {
        final Instance instance = MinecraftServer.getInstanceManager().getInstance(worldId);
        if (!(instance instanceof final DSWorld world)) {
            return;
        }
        final List<CompletableFuture<Void>> teleportFutures = new ArrayList<>();
        for (final Player player : instance.getPlayers()) {
            teleportFutures.add(this.getLobbyWorld().thenAccept(lobby -> TeleportUtil.teleportEntity(player, lobby, lobby.getSpawnPointFor((DSPlayer) player).asPos())));
        }
        CompletableFuture.allOf(teleportFutures.toArray(CompletableFuture[]::new))
                .thenCompose(_ -> world.save())
                .thenRun(() -> {
                    try {
                        MinecraftServer.getInstanceManager().unregisterInstance(instance);
                        final Path path = world.worldFolder();
                        FileUtil.move(path, Path.of("deleted").resolve(path.resolveSibling(path.getFileName() + "-deleted")));
                    } catch (Exception e) {
                        DSLogger.getLogger().severe(e);
                    }
                })
                .whenComplete((r, e) -> {
                    if (e != null) {
                        DSLogger.getLogger().severe(e);
                    }
                });
    }

    @Override
    public ReadWriteLock lock() {
        return this.lock;
    }
}
