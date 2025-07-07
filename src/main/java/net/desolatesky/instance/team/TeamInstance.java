package net.desolatesky.instance.team;

import net.desolatesky.block.BlockTags;
import net.desolatesky.breaking.BreakingManager;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.instance.InstancePos;
import net.desolatesky.instance.generator.StartingIslandGenerator;
import net.desolatesky.instance.weather.WeatherManager;
import net.desolatesky.player.DSPlayer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;
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

public final class TeamInstance extends InstanceContainer implements DSInstance {

    private static final Pos initialSpawnPoint = new Pos(0.5, 64, 0.5);
    private final RandomGenerator randomSource = new SplittableRandom();
    private final BreakingManager breakingManager = new BreakingManager(new HashMap<>());
    private final WeatherManager weatherManager = new WeatherManager(this, this.randomSource);
    private UUID owner;
    private InstancePos spawnPoint;
    private Task tickTask;

    public static @Nullable TeamInstance load(InstanceManager instanceManager, UUID ownerUUID, Path worldFolderPath) {
        final Path worldPath = worldFolderPath.resolve(ownerUUID.toString()).resolve("world");
        if (!worldPath.toFile().exists()) {
            return null;
        }
        final TeamInstance teamInstance = new TeamInstance(ownerUUID, worldPath, initialSpawnPoint);
        instanceManager.registerInstance(teamInstance);
        teamInstance.load();
        return teamInstance;
    }

    public static TeamInstance create(InstanceManager instanceManager, UUID ownerUUID, Path worldFolderPath) {
        final TeamInstance teamInstance = new TeamInstance(ownerUUID, worldFolderPath.resolve(ownerUUID.toString()).resolve("world"), initialSpawnPoint);
        instanceManager.registerInstance(teamInstance);
        teamInstance.load();
        return teamInstance;
    }

    private TeamInstance(UUID ownerUUID, Path worldFolderPath, Pos spawn) {
        super(ownerUUID, DimensionType.OVERWORLD, new AnvilLoader(worldFolderPath));
        this.owner = ownerUUID;
        this.spawnPoint = new InstancePos(this, spawn);
        this.setGenerator(new StartingIslandGenerator(this));
        this.setChunkSupplier(LightingChunk::new);
    }

    public CompletableFuture<Void> unload() {
        this.tickTask.cancel();
        return this.saveChunksToStorage().whenComplete((result, error) -> this.saveInstance().join());
    }

    public Point initialSpawnPoint() {
        return initialSpawnPoint;
    }

    @Override
    public InstancePos getSpawnPoint() {
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
    public void breakBlock(DSPlayer player, BlockVec pos, Block block) {
        if (block.registry().material() == Material.GRASS_BLOCK) {
            this.setBlock(pos, Block.DIRT.withTag(BlockTags.BREAK_TIME, 2_000));
        } else {
            this.setBlock(pos, Block.GRASS_BLOCK.withTag(BlockTags.BREAK_TIME, 2_000));
        }
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
