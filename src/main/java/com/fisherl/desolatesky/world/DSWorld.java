package com.fisherl.desolatesky.world;

import com.fisherl.desolatesky.breaking.BreakingManager;
import com.fisherl.desolatesky.player.DSPlayer;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;

import java.util.UUID;
import java.util.function.Function;
import java.util.random.RandomGenerator;

public interface DSWorld extends Block.Getter, Block.Setter {

    UUID worldId();

    void setBlock(Point pos, Key blockId, Function<Block, Block> blockTransformer);

    RandomGenerator getRandomGenerator(Point pos);

    boolean rollChance(Point pos, double chance);

    boolean canBreakBlock(DSPlayer player, Point blockPosition, Block block);

    BreakingManager breakingManager();

}
