package net.desolatesky.world;

import net.desolatesky.block.BlockFactory;
import net.desolatesky.breaking.AdminBreakingManager;
import net.desolatesky.breaking.BreakingManager;
import net.desolatesky.entity.EntityFactory;
import net.desolatesky.item.ItemFactory;
import net.desolatesky.loot.LootFactory;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.recipe.RecipeFactory;
import net.desolatesky.util.Constants;
import net.desolatesky.world.biome.Biomes;
import net.desolatesky.world.region.Region;
import net.desolatesky.world.region.SquareRegion;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.WorldBorder;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.Generator;
import net.minestom.server.registry.RegistryKey;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.UnknownNullability;

import java.nio.file.Path;
import java.util.SplittableRandom;
import java.util.UUID;
import java.util.function.Function;
import java.util.random.RandomGenerator;

@NotNullByDefault
public final class LobbyWorld extends DSWorld {

    public static final Pos SPAWN = new Pos(0, 64, 0);

    private static final double DIAMETER = 100;
    private static final RandomGenerator RANDOM_GENERATOR = new SplittableRandom(0);
    public static final UUID ID = new UUID(0, 0);
    private final Pos spawn;
    private final SquareRegion region;

    public LobbyWorld(
            RandomGenerator randomGenerator,
            BlockFactory blockFactory,
            ItemFactory itemFactory,
            EntityFactory entityFactory,
            LootFactory lootFactory,
            RecipeFactory recipeFactory,
            RegistryKey<DimensionType> dimensionType,
            Path worldFolder
    ) {
        super(unit -> unit.modifier().fillBiome(Biomes.desolateBiome()),
                randomGenerator, blockFactory,
                itemFactory,
                entityFactory,
                lootFactory,
                recipeFactory,
                new AdminBreakingManager(),
                ID,
                dimensionType,
                worldFolder.resolve("lobby"),
                0);
        this.spawn = SPAWN;
        this.region = Region.square(this.spawn, DIAMETER, Constants.WORLD_MIN_Y, Constants.WORLD_MAX_Y);
        this.setWorldBorder(new WorldBorder(DIAMETER, this.spawn.x(), this.spawn.z(), 0, 0, (int) DIAMETER));
    }

    @Override
    public void setBlock(Point pos, Key blockId, Function<Block, Block> blockTransformer) {
    }

    @Override
    public RandomGenerator getRandomGenerator(Point pos) {
        return RANDOM_GENERATOR;
    }

    @Override
    public boolean rollChance(Point pos, double chance) {
        return false;
    }

    @Override
    public boolean canBreakBlock(DSPlayer player, Point blockPosition, Block block) {
        return false;
    }

    @Override
    public boolean canPlaceBlock(DSPlayer player, Point blockPosition, Block block) {
        return false;
    }

    @Override
    public @UnknownNullability Block getBlock(int x, int y, int z, Condition condition) {
        return super.getBlock(x, y, z, condition);
    }

    @Override
    public void setBlock(int x, int y, int z, Block block) {

    }

    @Override
    public void breakBlock(DSPlayer player, Point blockPosition) {

    }

    @Override
    public Point getSpawnPointFor(DSPlayer player) {
        return this.spawn;
    }

    @Override
    public SquareRegion getRegion() {
        return this.region;
    }


    @Override
    public void sendWorldBorder(Player player) {

    }

    @Override
    public WorldType worldType() {
        return WorldType.LOBBY;
    }
}
