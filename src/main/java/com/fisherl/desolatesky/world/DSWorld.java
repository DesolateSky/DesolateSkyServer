package com.fisherl.desolatesky.world;

import com.fisherl.desolatesky.block.BlockFactory;
import com.fisherl.desolatesky.breaking.BreakingManager;
import com.fisherl.desolatesky.entity.EntityFactory;
import com.fisherl.desolatesky.item.ItemFactory;
import com.fisherl.desolatesky.loot.LootFactory;
import com.fisherl.desolatesky.player.DSPlayer;
import com.fisherl.desolatesky.recipe.RecipeFactory;
import com.fisherl.desolatesky.world.region.SquareRegion;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
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

    Point getSpawnPointFor(DSPlayer player);

    SquareRegion getRegion();

    Instance asInstance();

    BlockFactory blockFactory();

    ItemFactory itemFactory();

    EntityFactory entityFactory();

    LootFactory lootFactory();

    RecipeFactory recipeFactory();

    void sendWorldBorder(Player player);
}
