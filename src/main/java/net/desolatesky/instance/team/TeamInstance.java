package net.desolatesky.instance.team;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.BlockTags;
import net.desolatesky.breaking.BreakingManager;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.instance.InstancePoint;
import net.desolatesky.instance.generator.StartingIslandGenerator;
import net.desolatesky.instance.weather.WeatherManager;
import net.desolatesky.player.DSPlayer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.WorldBorder;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.SplittableRandom;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.random.RandomGenerator;

public final class TeamInstance extends DSInstance {

    private static final Pos initialSpawnPoint = new Pos(0.5, 64, 0.5);
    private final RandomGenerator randomSource = new SplittableRandom();
    private final BreakingManager breakingManager = new BreakingManager(new HashMap<>());
    private final WeatherManager weatherManager;
    private UUID owner;
    private InstancePoint<Pos> spawnPoint;
    private Task tickTask;

    public static @Nullable TeamInstance load(DesolateSkyServer server, InstanceManager instanceManager, UUID ownerUUID, Path worldFolderPath) {
        final Path worldPath = worldFolderPath.resolve(ownerUUID.toString()).resolve("world");
        if (!worldPath.toFile().exists()) {
            return null;
        }
        final TeamInstance teamInstance = new TeamInstance(server, ownerUUID, worldPath, initialSpawnPoint);
        instanceManager.registerInstance(teamInstance);
        teamInstance.load();
        return teamInstance;
    }

    public static TeamInstance create(DesolateSkyServer server, InstanceManager instanceManager, UUID ownerUUID, Path worldFolderPath) {
        final TeamInstance teamInstance = new TeamInstance(server, ownerUUID, worldFolderPath.resolve(ownerUUID.toString()).resolve("world"), initialSpawnPoint);
        instanceManager.registerInstance(teamInstance);
        teamInstance.load();
        return teamInstance;
    }

    private TeamInstance(DesolateSkyServer server, UUID ownerUUID, Path worldFolderPath, Pos spawn) {
        super(ownerUUID, DimensionType.OVERWORLD, new AnvilLoader(worldFolderPath));
        this.owner = ownerUUID;
        this.spawnPoint = new InstancePoint<>(this, spawn);
        this.weatherManager = new WeatherManager(server.blocks(), server.entityLootRegistry(), this, this.randomSource);
        this.setGenerator(new StartingIslandGenerator(server.blocks(), this));
        this.setChunkSupplier(LightingChunk::new);
        this.setWorldBorder(new WorldBorder(50, spawn.x(), spawn.z(), 0, 0, 50));
    }

    public CompletableFuture<Void> unload() {
        this.tickTask.cancel();
        return this.saveChunksToStorage().whenComplete((result, error) -> this.saveInstance().join());
    }

    public Point initialSpawnPoint() {
        return initialSpawnPoint;
    }

    @Override
    public InstancePoint<Pos> getDefaultSpawnPoint() {
        return this.spawnPoint;
    }

    @Override
    public void onLeave(DSPlayer player) {
        this.weatherManager.handlePlayerLeave(player);
    }

    private void load() {
        this.tickTask = this.scheduler().scheduleTask(this::onTick, TaskSchedule.nextTick(), TaskSchedule.nextTick());
    }

    private void onTick() {
        this.weatherManager.tick();
        this.breakingManager.tick();
    }

    @Override
    public void breakBlock(DSPlayer player, BlockVec pos, Block block, BlockFace face) {
        this.breakBlock(player, pos, face, true);
    }

    @Override
    public BreakingManager breakingManager() {
        return this.breakingManager;
    }

    @Override
    public boolean canBreakBlock(DSPlayer player, BlockVec pos, Block block) {
        return !block.hasTag(BlockTags.UNBREAKABLE);
    }

    @Override
    public WeatherManager weatherManager() {
        return this.weatherManager;
    }

    @Override
    public RandomGenerator randomSource() {
        return this.randomSource;
    }

    public UUID owner() {
        return this.owner;
    }

}
