package net.desolatesky.instance.lobby;

import net.desolatesky.breaking.BreakingManager;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.instance.InstancePos;
import net.desolatesky.instance.region.Region;
import net.desolatesky.instance.weather.WeatherManager;
import net.desolatesky.player.DSPlayer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.instance.block.Block;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.world.DimensionType;
import net.minestom.server.world.biome.Biome;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.SplittableRandom;
import java.util.UUID;
import java.util.random.RandomGenerator;

public final class LobbyInstance extends InstanceContainer implements DSInstance {

    public static LobbyInstance createLobby(UUID uuid, Path worldFolderPath, Pos spawnPoint, Region region) {
        final Path worldPath = worldFolderPath.resolve("world");
        final LobbyInstance lobbyInstance = new LobbyInstance(uuid, worldPath, spawnPoint, region);
        lobbyInstance.load();
        return lobbyInstance;
    }

    private final RandomGenerator randomSource = new SplittableRandom();

    private final BreakingManager breakingManager = new BreakingManager(new HashMap<>());
    private final Path worldPath;
    private final InstancePos spawnPoint;
    private final Region region;
    private final WeatherManager weatherManager = new WeatherManager(this, this.randomSource);

    private LobbyInstance(UUID uuid, Path worldPath, Pos spawnPoint, Region region) {
        super(uuid, DimensionType.OVERWORLD);
        this.worldPath = worldPath;
        this.spawnPoint = new InstancePos(this, spawnPoint);
        this.region = region;
        this.setChunkLoader(new AnvilLoader(worldPath));
        this.setGenerator(unit -> unit.modifier().fillBiome(Biome.THE_VOID));
    }

    @Override
    public InstancePos getSpawnPoint() {
        return this.spawnPoint;
    }

    public Region region() {
        return this.region;
    }

    @Override
    public void onLeave(DSPlayer player) {

    }

    @Override
    public void breakBlock(DSPlayer player, BlockVec pos, Block block) {

    }

    private void load() {
        this.scheduler().scheduleTask(this::onTick, TaskSchedule.nextTick(), TaskSchedule.nextTick());
    }

    private void onTick() {
        this.breakingManager.tick();
    }


    @Override
    public BreakingManager breakingManager() {
        return this.breakingManager;
    }

    @Override
    public boolean canBreakBlock(DSPlayer player, BlockVec pos, Block block) {
        return false;
    }

    @Override
    public WeatherManager weatherManager() {
        return this.weatherManager;
    }

    @Override
    public RandomGenerator randomSource() {
        return this.randomSource;
    }

}
