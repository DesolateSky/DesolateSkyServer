package net.desolatesky.world;

import net.desolatesky.block.BlockFactory;
import net.desolatesky.breaking.ConfiguredBreakingManager;
import net.desolatesky.entity.EntityFactory;
import net.desolatesky.island.Island;
import net.desolatesky.island.permission.IslandPermission;
import net.desolatesky.item.ItemFactory;
import net.desolatesky.loot.LootFactory;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.recipe.RecipeFactory;
import net.desolatesky.util.Constants;
import net.desolatesky.world.region.Region;
import net.desolatesky.world.region.SquareRegion;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.WorldBorder;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.server.play.WorldBorderSizePacket;
import net.minestom.server.registry.RegistryKey;
import net.minestom.server.world.DimensionType;

import java.nio.file.Path;
import java.util.random.RandomGenerator;

public final class PlayerWorld extends DSWorld implements IslandWorld {

    public static final Point DEFAULT_SPAWN_POINT = new Pos(0.5, Constants.WORLD_MIN_Y + 8, 0.5);
    public static final SquareRegion STARTING_REGION = Region.square(DEFAULT_SPAWN_POINT.asBlockVec(), 7, Constants.WORLD_MIN_Y, Constants.WORLD_MAX_Y);
    public static final SquareRegion MAX_REGION_SIZE = Region.square(DEFAULT_SPAWN_POINT.asBlockVec(), Constants.MAX_WORLD_RADIUS, Constants.WORLD_MIN_Y, Constants.WORLD_MAX_Y);
    public static final SquareRegion SPAWN_PLATFORM_SIZE = Region.square(DEFAULT_SPAWN_POINT.asBlockVec(), 3, Constants.WORLD_MIN_Y, Constants.WORLD_MAX_Y);

    private final Island island;

    public PlayerWorld(
            RandomGenerator randomGenerator,
            BlockFactory blockFactory,
            ItemFactory itemFactory,
            EntityFactory entityFactory,
            LootFactory lootFactory,
            RecipeFactory recipeFactory,
            RegistryKey<DimensionType> dimensionType,
            Path worldFolder,
            Island island
    ) {
        super(new PlayerWorldGenerator(blockFactory),
                randomGenerator,
                blockFactory,
                itemFactory,
                entityFactory,
                lootFactory,
                recipeFactory,
                new ConfiguredBreakingManager(blockFactory),
                island.getWorldId(WorldType.ISLAND),
                dimensionType,
                worldFolder,
                3);
        this.island = island;
        final SquareRegion region = this.island.worldSize();
        final Point center = region.center();
        this.setWorldBorder(new WorldBorder(region.radius() * 2 + 2, center.x() + 0.5, center.z() + 0.5, 0, 0, (int) region.radius() * 2 + 2));
    }

    @Override
    public void sendWorldBorder(Player player) {
        player.sendPacket(new WorldBorderSizePacket(this.island.worldSize().radius() * 2 + 1));
    }

    @Override
    public boolean canBreakBlock(DSPlayer player, Point blockPosition, Block block) {
        return this.island.hasPermission(player.getUuid(), IslandPermission.BREAK_BLOCK);
    }

    @Override
    public Point getSpawnPointFor(DSPlayer player) {
        return DEFAULT_SPAWN_POINT;
    }

    @Override
    public SquareRegion getRegion() {
        return this.island.worldSize();
    }

    @Override
    public Island island() {
        return this.island;
    }

    @Override
    public WorldType worldType() {
        return WorldType.ISLAND;
    }
}
