package net.desolatesky.instance;

import net.desolatesky.block.entity.BlockEntity;
import net.desolatesky.breaking.BreakingManager;
import net.desolatesky.instance.weather.WeatherManager;
import net.desolatesky.player.DSPlayer;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.IChunkLoader;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.registry.DynamicRegistry;
import net.minestom.server.registry.RegistryKey;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.random.RandomGenerator;

public abstract class DSInstance extends InstanceContainer {

    protected final Path worldFilePath;
    protected final Map<UUID, InstancePoint<Pos>> playerSpawnPoints = new HashMap<>();
    protected final Set<Point> blockEntities = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private final ReadWriteLock blockEntityLock = new ReentrantReadWriteLock();

    public DSInstance(@NotNull UUID uuid, Path worldFilePath, @NotNull RegistryKey<DimensionType> dimensionType) {
        super(uuid, dimensionType);
        this.worldFilePath = worldFilePath;
    }

    public DSInstance(@NotNull UUID uuid, Path worldFilePath, @NotNull RegistryKey<DimensionType> dimensionType, @NotNull Key dimensionName) {
        super(uuid, dimensionType, dimensionName);
        this.worldFilePath = worldFilePath;
    }

    public DSInstance(@NotNull UUID uuid, Path worldFilePath, @NotNull RegistryKey<DimensionType> dimensionType, @Nullable IChunkLoader loader) {
        super(uuid, dimensionType, loader);
        this.worldFilePath = worldFilePath;
    }

    public DSInstance(@NotNull UUID uuid, Path worldFilePath, @NotNull RegistryKey<DimensionType> dimensionType, @Nullable IChunkLoader loader, @NotNull Key dimensionName) {
        super(uuid, dimensionType, loader, dimensionName);
        this.worldFilePath = worldFilePath;
    }

    public DSInstance(@NotNull DynamicRegistry<DimensionType> dimensionTypeRegistry, Path worldFilePath, @NotNull UUID uuid, @NotNull RegistryKey<DimensionType> dimensionType, @Nullable IChunkLoader loader, @NotNull Key dimensionName) {
        super(dimensionTypeRegistry, uuid, dimensionType, loader, dimensionName);
        this.worldFilePath = worldFilePath;
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
                    this.setBlock(point, saved);
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
