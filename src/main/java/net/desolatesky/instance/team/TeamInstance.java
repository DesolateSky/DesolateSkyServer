package net.desolatesky.instance.team;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.DSBlockRegistry;
import net.desolatesky.block.handler.BlockHandlerResult;
import net.desolatesky.block.handler.DSBlockHandler;
import net.desolatesky.breaking.BreakingManager;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.instance.InstancePoint;
import net.desolatesky.instance.chunk.DSChunk;
import net.desolatesky.instance.generator.StartingIslandGenerator;
import net.desolatesky.instance.weather.WeatherManager;
import net.desolatesky.item.DSItemRegistry;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.team.IslandTeam;
import net.desolatesky.team.role.RolePermissionType;
import net.desolatesky.util.RandomUtil;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.Section;
import net.minestom.server.instance.WorldBorder;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.SplittableRandom;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.random.RandomGenerator;

public final class TeamInstance extends DSInstance {

    // blocks to random tick per section
    private final int blocksToRandomTick = 100;

    private static final Pos initialSpawnPoint = new Pos(0.5, 64, 0.5);
    private final RandomGenerator randomSource = new SplittableRandom();
    private final BreakingManager breakingManager;
    private final WeatherManager weatherManager;
    private final DSBlockRegistry blockRegistry;
    private final DSItemRegistry itemRegistry;
    private final IslandTeam team;
    private InstancePoint<Pos> spawnPoint;
    private Task tickTask;

    public static @Nullable TeamInstance load(DesolateSkyServer server, InstanceManager instanceManager, IslandTeam team, Path worldFolderPath) {
        final Path worldPath = worldFolderPath.resolve(team.id().toString()).resolve("world");
        if (!worldPath.toFile().exists()) {
            return null;
        }
        final TeamInstance teamInstance = new TeamInstance(server, team, worldPath, initialSpawnPoint);
        instanceManager.registerInstance(teamInstance);
        teamInstance.load();
        return teamInstance;
    }

    public static TeamInstance create(DesolateSkyServer server, InstanceManager instanceManager, IslandTeam team, Path worldFolderPath) {
        final TeamInstance teamInstance = new TeamInstance(server, team, worldFolderPath.resolve(team.id().toString()).resolve("world"), initialSpawnPoint);
        instanceManager.registerInstance(teamInstance);
        teamInstance.load();
        return teamInstance;
    }

    private TeamInstance(DesolateSkyServer server, IslandTeam team, Path worldFolderPath, Pos spawn) {
        super(server.blockRegistry(), team.id(), worldFolderPath, DimensionType.OVERWORLD, new AnvilLoader(worldFolderPath));
        this.breakingManager = new BreakingManager(server, new HashMap<>(), server.blockRegistry());
        this.team = team;
        this.spawnPoint = new InstancePoint<>(this, spawn);
        this.weatherManager = new WeatherManager(server.entityLootRegistry(), this, this.randomSource);
        this.blockRegistry = server.blockRegistry();
        this.itemRegistry = server.itemRegistry();
        this.setGenerator(new StartingIslandGenerator(server.blockEntities(), this));
        this.setChunkSupplier((instance, chunkX, chunkZ) -> new DSChunk(this.blockRegistry, (DSInstance) instance, chunkX, chunkZ));
        this.setWorldBorder(new WorldBorder(50, spawn.x(), spawn.z(), 0, 0, 50));
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
        this.tickTask = this.scheduler().scheduleTask(this::tick, TaskSchedule.nextTick(), TaskSchedule.nextTick());
    }

    @Override
    protected void tick() {
        super.tick();
        this.weatherManager.tick();
        this.breakingManager.tick();
        this.randomTick();
    }

    private void randomTick() {
        for (final Chunk chunk : this.getChunks()) {
            if (!chunk.isLoaded()) {
                continue;
            }
            this.randomTickChunk(chunk);
        }
    }

    private void randomTickChunk(Chunk chunk) {
        final Set<Point> ticked = new HashSet<>();
        final int chunkX = chunk.getChunkX();
        final int chunkZ = chunk.getChunkZ();
        for (final Section section : chunk.getSections()) {
            for (int i = 0; i < this.blocksToRandomTick; i++) {
                final Point blockPoint = RandomUtil.randomBlockPos(this.randomSource, section, chunkX, i, chunkZ);
                if (!ticked.add(blockPoint)) {
                    continue;
                }
                final Block block = this.getBlock(blockPoint);
                final DSBlockHandler blockHandler = this.blockRegistry.getHandlerForBlock(block);
                if (blockHandler == null) {
                    continue;
                }
                blockHandler.onRandomTick(this, block, blockPoint);
            }
        }
    }

    @Override
    public void breakBlock(DSPlayer player, BlockVec pos, Block block, BlockFace face) {
        final DSBlockHandler blockHandler = this.blockRegistry.getHandlerForBlock(block);
        if (blockHandler == null || !this.canBreakBlock(player, pos, block)) {
            return;
        }
        blockHandler.playerDestroyBlock(this.itemRegistry, player, this, block, pos);
    }

    @Override
    public BreakingManager breakingManager() {
        return this.breakingManager;
    }

    @Override
    public boolean canBreakBlock(DSPlayer player, Point pos, Block block) {
        return this.team.hasPermission(player, RolePermissionType.BREAK_BLOCK, block.key());
    }

    @Override
    public WeatherManager weatherManager() {
        return this.weatherManager;
    }

    @Override
    public RandomGenerator randomSource() {
        return this.randomSource;
    }

    public UUID getOwnerId() {
        return this.team.getOwnerId();
    }

    public IslandTeam team() {
        return this.team;
    }

}
