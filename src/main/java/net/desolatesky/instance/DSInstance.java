package net.desolatesky.instance;

import net.desolatesky.breaking.BreakingManager;
import net.desolatesky.instance.weather.WeatherManager;
import net.desolatesky.player.DSPlayer;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.BlockVec;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.random.RandomGenerator;

public abstract class DSInstance extends InstanceContainer {

    protected final Map<UUID, InstancePoint<Pos>> playerSpawnPoints = new HashMap<>();

    public DSInstance(@NotNull UUID uuid, @NotNull RegistryKey<DimensionType> dimensionType) {
        super(uuid, dimensionType);
    }

    public DSInstance(@NotNull UUID uuid, @NotNull RegistryKey<DimensionType> dimensionType, @NotNull Key dimensionName) {
        super(uuid, dimensionType, dimensionName);
    }

    public DSInstance(@NotNull UUID uuid, @NotNull RegistryKey<DimensionType> dimensionType, @Nullable IChunkLoader loader) {
        super(uuid, dimensionType, loader);
    }

    public DSInstance(@NotNull UUID uuid, @NotNull RegistryKey<DimensionType> dimensionType, @Nullable IChunkLoader loader, @NotNull Key dimensionName) {
        super(uuid, dimensionType, loader, dimensionName);
    }

    public DSInstance(@NotNull DynamicRegistry<DimensionType> dimensionTypeRegistry, @NotNull UUID uuid, @NotNull RegistryKey<DimensionType> dimensionType, @Nullable IChunkLoader loader, @NotNull Key dimensionName) {
        super(dimensionTypeRegistry, uuid, dimensionType, loader, dimensionName);
    }

    public abstract  InstancePoint<Pos> getDefaultSpawnPoint();

    public InstancePoint<Pos> getSpawnPointFor(DSPlayer player) {
        return this.playerSpawnPoints.getOrDefault(player.getUuid(), this.getDefaultSpawnPoint());
    }

    public void setPlayerSpawnPoint(DSPlayer player, Pos spawnPoint) {
        this.playerSpawnPoints.put(player.getUuid(), new InstancePoint<>(this, spawnPoint));
    }

    public abstract void onLeave(DSPlayer player);

    public abstract void breakBlock(DSPlayer player, BlockVec pos, Block block, BlockFace face);

    public abstract BreakingManager breakingManager();

    public abstract boolean canBreakBlock(DSPlayer player, BlockVec pos, Block block);

    public abstract WeatherManager weatherManager();

    public abstract RandomGenerator randomSource();

}
