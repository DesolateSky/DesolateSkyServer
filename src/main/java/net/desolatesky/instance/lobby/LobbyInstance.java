package net.desolatesky.instance.lobby;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.breaking.BreakingManager;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.instance.InstancePoint;
import net.desolatesky.instance.region.Region;
import net.desolatesky.instance.weather.WeatherManager;
import net.desolatesky.player.DSPlayer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.world.DimensionType;
import net.minestom.server.world.biome.Biome;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.SplittableRandom;
import java.util.UUID;
import java.util.random.RandomGenerator;

public final class LobbyInstance extends DSInstance {

    public static LobbyInstance createLobby(DesolateSkyServer server, UUID uuid, Path worldFolderPath, Pos spawnPoint, Region region) {
        final Path worldPath = worldFolderPath.resolve("world");
        final LobbyInstance lobbyInstance = new LobbyInstance(server, uuid, worldPath, spawnPoint, region);
        lobbyInstance.load();
        return lobbyInstance;
    }

    private final RandomGenerator randomSource = new SplittableRandom();

    private final BreakingManager breakingManager;
    private final Path worldPath;
    private final InstancePoint<Pos> spawnPoint;
    private final Region region;
    private final WeatherManager weatherManager;

    private LobbyInstance(DesolateSkyServer server, UUID uuid, Path worldPath, Pos spawnPoint, Region region) {
        super(server.blockRegistry(), uuid, worldPath, DimensionType.OVERWORLD, new AnvilLoader(worldPath));
        this.breakingManager = new BreakingManager(server, new HashMap<>(), server.blockRegistry());
        this.worldPath = worldPath;
        this.spawnPoint = new InstancePoint<>(this, spawnPoint);
        this.region = region;
        this.weatherManager = new WeatherManager(server.entityLootRegistry(), this, this.randomSource);
        this.setGenerator(unit -> unit.modifier().fillBiome(Biome.THE_VOID));
    }

    @Override
    public InstancePoint<Pos> getDefaultSpawnPoint() {
        return this.spawnPoint;
    }

    public Region region() {
        return this.region;
    }

    @Override
    public void onLeave(DSPlayer player) {

    }

    @Override
    public void breakBlock(DSPlayer player, BlockVec pos, Block block, BlockFace face) {

    }

    @Override
    protected void onTick() {
    }

    @Override
    public BreakingManager breakingManager() {
        return this.breakingManager;
    }

    @Override
    public boolean canBreakBlock(DSPlayer player, Point pos, Block block) {
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
