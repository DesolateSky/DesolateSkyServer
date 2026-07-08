package com.fisherl.desolatesky.world.lobby;

import com.fisherl.desolatesky.block.BlockFactory;
import com.fisherl.desolatesky.breaking.AdminBreakingManager;
import com.fisherl.desolatesky.breaking.BreakingManager;
import com.fisherl.desolatesky.entity.EntityFactory;
import com.fisherl.desolatesky.item.ItemFactory;
import com.fisherl.desolatesky.loot.LootFactory;
import com.fisherl.desolatesky.player.DSPlayer;
import com.fisherl.desolatesky.recipe.RecipeFactory;
import com.fisherl.desolatesky.util.Constants;
import com.fisherl.desolatesky.world.DSWorld;
import com.fisherl.desolatesky.world.biome.Biomes;
import com.fisherl.desolatesky.world.region.Region;
import com.fisherl.desolatesky.world.region.SquareRegion;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.WorldBorder;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.instance.block.Block;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.UnknownNullability;

import java.nio.file.Path;
import java.util.SplittableRandom;
import java.util.UUID;
import java.util.function.Function;
import java.util.random.RandomGenerator;

@NotNullByDefault
public final class LobbyWorld extends InstanceContainer implements DSWorld {

    private static final double DIAMETER = 100;
    private static final RandomGenerator RANDOM_GENERATOR = new SplittableRandom(0);
    public static final UUID ID = new UUID(0, 0);
    private final BreakingManager breakingManager = new AdminBreakingManager();
    private final BlockFactory blockFactory;
    private final ItemFactory itemFactory;
    private final EntityFactory entityFactory;
    private final LootFactory lootFactory;
    private final RecipeFactory recipeFactory;
    private final Point spawn;
    private final SquareRegion region;
    private final Path worldFolderPath;

    public LobbyWorld(Path worldsFolderPath,
                      BlockFactory blockFactory,
                      ItemFactory itemFactory,
                      EntityFactory entityFactory,
                      LootFactory lootFactory,
                      RecipeFactory recipeFactory) {
        super(ID, DimensionType.OVERWORLD);
        this.worldFolderPath = worldsFolderPath.resolve("lobby");
        this.setChunkLoader(new AnvilLoader(this.worldFolderPath));
        this.blockFactory = blockFactory;
        this.itemFactory = itemFactory;
        this.entityFactory = entityFactory;
        this.lootFactory = lootFactory;
        this.recipeFactory = recipeFactory;
        this.spawn = new Pos(0, 64, 0);
        this.region = Region.square(this.spawn, DIAMETER, Constants.WORLD_MIN_Y, Constants.WORLD_MAX_Y);
        this.setWorldBorder(new WorldBorder(DIAMETER, this.spawn.x(), this.spawn.z(), 0, 0, (int) DIAMETER));
        this.setGenerator(unit -> unit.modifier().fillBiome(Biomes.desolateBiome()));
    }

    @Override
    public UUID worldId() {
        return ID;
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
    public BreakingManager breakingManager() {
        return this.breakingManager;
    }

    @Override
    public @UnknownNullability Block getBlock(int x, int y, int z, Condition condition) {
        return super.getBlock(x, y, z, condition);
    }

    @Override
    public void setBlock(int x, int y, int z, Block block) {

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
    public Instance asInstance() {
        return this;
    }

    @Override
    public BlockFactory blockFactory() {
        return this.blockFactory;
    }

    @Override
    public ItemFactory itemFactory() {
        return this.itemFactory;
    }

    @Override
    public EntityFactory entityFactory() {
        return this.entityFactory;
    }

    @Override
    public LootFactory lootFactory() {
        return this.lootFactory;
    }

    @Override
    public RecipeFactory recipeFactory() {
        return this.recipeFactory;
    }

    @Override
    public void sendWorldBorder(Player player) {

    }
}
