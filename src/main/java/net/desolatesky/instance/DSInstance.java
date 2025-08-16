package net.desolatesky.instance;

import net.desolatesky.block.DSBlockRegistry;
import net.desolatesky.block.entity.BlockEntity;
import net.desolatesky.block.handler.DSBlockHandler;
import net.desolatesky.breaking.BreakingManager;
import net.desolatesky.instance.weather.WeatherManager;
import net.desolatesky.player.DSPlayer;
import net.kyori.adventure.key.Key;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.IChunkLoader;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.registry.DynamicRegistry;
import net.minestom.server.registry.RegistryKey;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.utils.Direction;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.random.RandomGenerator;

public abstract class DSInstance extends InstanceContainer {

    protected final Path worldFilePath;
    protected final Map<UUID, InstancePoint<Pos>> playerSpawnPoints = new HashMap<>();
    protected final Set<Point> blockEntities = Collections.newSetFromMap(new ConcurrentHashMap<>());
    protected final Queue<Point> scheduledBlockUpdates = new ConcurrentLinkedDeque<>();
    protected final DSBlockRegistry blockRegistry;

    private Task tickTask;
    private long currentTick;

    private final ReadWriteLock blockEntityLock = new ReentrantReadWriteLock();
    protected final ReadWriteLock blockUpdateLock = new ReentrantReadWriteLock();


    public DSInstance(DSBlockRegistry blockRegistry, @NotNull UUID uuid, Path worldFilePath, @NotNull RegistryKey<DimensionType> dimensionType) {
        super(uuid, dimensionType);
        this.worldFilePath = worldFilePath;
        this.blockRegistry = blockRegistry;
    }

    public DSInstance(DSBlockRegistry blockRegistry, @NotNull UUID uuid, Path worldFilePath, @NotNull RegistryKey<DimensionType> dimensionType, @NotNull Key dimensionName) {
        super(uuid, dimensionType, dimensionName);
        this.worldFilePath = worldFilePath;
        this.blockRegistry = blockRegistry;
    }

    public DSInstance(DSBlockRegistry blockRegistry, @NotNull UUID uuid, Path worldFilePath, @NotNull RegistryKey<DimensionType> dimensionType, @Nullable IChunkLoader loader) {
        super(uuid, dimensionType, loader);
        this.worldFilePath = worldFilePath;
        this.blockRegistry = blockRegistry;
    }

    public DSInstance(DSBlockRegistry blockRegistry, @NotNull UUID uuid, Path worldFilePath, @NotNull RegistryKey<DimensionType> dimensionType, @Nullable IChunkLoader loader, @NotNull Key dimensionName) {
        super(uuid, dimensionType, loader, dimensionName);
        this.worldFilePath = worldFilePath;
        this.blockRegistry = blockRegistry;
    }

    public DSInstance(DSBlockRegistry blockRegistry, @NotNull DynamicRegistry<DimensionType> dimensionTypeRegistry, Path worldFilePath, @NotNull UUID uuid, @NotNull RegistryKey<DimensionType> dimensionType, @Nullable IChunkLoader loader, @NotNull Key dimensionName) {
        super(dimensionTypeRegistry, uuid, dimensionType, loader, dimensionName);
        this.worldFilePath = worldFilePath;
        this.blockRegistry = blockRegistry;
    }

    public abstract InstancePoint<Pos> getDefaultSpawnPoint();

    public InstancePoint<Pos> getSpawnPointFor(DSPlayer player) {
        return this.playerSpawnPoints.getOrDefault(player.getUuid(), this.getDefaultSpawnPoint());
    }

    public void setPlayerSpawnPoint(DSPlayer player, Pos spawnPoint) {
        this.playerSpawnPoints.put(player.getUuid(), new InstancePoint<>(this, spawnPoint));
    }

    public abstract void onLeave(DSPlayer player);

    public abstract void breakBlock(DSPlayer player, BlockVec pos, Block block, BlockFace face);

    public abstract BreakingManager breakingManager();

    public abstract boolean canBreakBlock(DSPlayer player, Point pos, Block block);

    public abstract WeatherManager weatherManager();

    public abstract RandomGenerator randomSource();

    public Path worldFilePath() {
        return this.worldFilePath;
    }

    public void onSave() {

    }

    public long currentTick() {
        return this.currentTick;
    }

    protected void load() {
        this.tickTask = this.scheduler().scheduleTask(this::tick, TaskSchedule.nextTick(), TaskSchedule.nextTick());
    }

    @Override
    public void setBlock(int x, int y, int z, @NotNull Block block, boolean doBlockUpdates) {
        super.setBlock(x, y, z, block, doBlockUpdates);
        if (!doBlockUpdates) {
            return;
        }
        this.blockUpdateLock.writeLock().lock();
        try {
            this.scheduledBlockUpdates.add(new BlockVec(x, y, z));
        } finally {
            this.blockUpdateLock.writeLock().unlock();
        }
    }

    @Override
    public boolean placeBlock(BlockHandler.@NotNull Placement placement, boolean doBlockUpdates) {
        final boolean result = super.placeBlock(placement, doBlockUpdates);
        if (!doBlockUpdates || !result) {
            return result;
        }
        this.scheduledBlockUpdates.add(placement.getBlockPosition().asBlockVec());
        this.blockUpdateLock.writeLock().lock();
        try {
            this.scheduledBlockUpdates.add(placement.getBlockPosition().asBlockVec());
        } finally {
            this.blockUpdateLock.writeLock().unlock();
        }
        return true;
    }

    @ApiStatus.Internal
    private void updateNeighbors(DSBlockRegistry blockRegistry, DSInstance instance, Point sourcePoint, Block sourceBlock) {
        for (Direction direction : Direction.values()) {
            final Point neighborPoint = sourcePoint.add(direction.vec());
            final Block neighbor = instance.getBlock(neighborPoint);
            final DSBlockHandler blockHandler = blockRegistry.getHandlerForBlock(neighbor);
            if (blockHandler == null) {
                continue;
            }
            blockHandler.onUpdate(instance, neighborPoint, neighbor, sourcePoint, sourceBlock);
        }
    }

    protected final void tick() {
        this.currentTick++;
        this.blockUpdateLock.writeLock().lock();
        try {
            final Set<Point> updatedPoints = new HashSet<>();
            while (!this.scheduledBlockUpdates.isEmpty()) {
                final Point point = this.scheduledBlockUpdates.poll();
                if (!updatedPoints.add(point)) {
                    continue;
                }
                final Block block = this.getBlock(point);
                this.updateNeighbors(this.blockRegistry, this, point, block);
            }
        } finally {
            this.blockUpdateLock.writeLock().unlock();
        }
        this.breakingManager().tick();
        this.onTick();
    }

    public CompletableFuture<Void> unload() {
        this.tickTask.cancel();
        return this.save()
                .whenComplete((unused, error) -> {
                    if (error != null) {
                        error.printStackTrace();
                    }
                    MinecraftServer.getInstanceManager().unregisterInstance(this);
                });
    }

    protected abstract void onTick();

    public final CompletableFuture<Void> save() {
        return CompletableFuture.runAsync(() -> {
            this.onSave();
            this.blockEntityLock.readLock().lock();
            final Collection<Point> blockEntitiesCopy = new HashSet<>();
            try {
                blockEntitiesCopy.addAll(this.blockEntities);
            } finally {
                this.blockEntityLock.readLock().unlock();
            }
            for (final Point point : blockEntitiesCopy) {
                final Block block = this.getBlock(point);
                if (!(block.handler() instanceof final BlockEntity<?> handler)) {
                    continue;
                }
                final Block saved = handler.save(this, point, block);
                if (saved != null) {
                    this.setBlock(point, saved, false);
                }
            }
            this.saveInstance().join();
            this.saveChunksToStorage().join();
        }).exceptionally(error -> {
            error.printStackTrace();
            return null;
        });
    }

    public void addBlockEntity(Point point) {
        this.blockEntityLock.writeLock().lock();
        try {
            this.blockEntities.add(point);
        } finally {
            this.blockEntityLock.writeLock().unlock();
        }
    }

    public void removeBlockEntity(Point point) {
        this.blockEntityLock.writeLock().lock();
        try {
            this.blockEntities.remove(point);
        } finally {
            this.blockEntityLock.writeLock().unlock();
        }
    }

}
