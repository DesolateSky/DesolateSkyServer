package net.desolatesky.instance;

import net.desolatesky.breaking.BreakingManager;
import net.desolatesky.instance.weather.WeatherManager;
import net.desolatesky.player.DSPlayer;
import net.minestom.server.adventure.audience.PacketGroupingAudience;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.block.Block;

import java.util.random.RandomGenerator;

public interface DSInstance extends PacketGroupingAudience {

    InstancePos getSpawnPoint();

    void onLeave(DSPlayer player);

    void breakBlock(DSPlayer player, BlockVec pos, Block block);

    BreakingManager breakingManager();

    boolean canBreakBlock(DSPlayer player, BlockVec pos, Block block);

    WeatherManager weatherManager();

    RandomGenerator randomSource();

}
