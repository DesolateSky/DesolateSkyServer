package com.fisherl.desolatesky.world;

import com.fisherl.desolatesky.block.BlockFactory;
import com.fisherl.desolatesky.block.behavior.BlockBehavior;
import com.fisherl.desolatesky.block.definition.BlockDefinition;
import com.fisherl.desolatesky.block.setting.BlockSetting;
import com.fisherl.desolatesky.breaking.ConfiguredBreakingManager;
import com.fisherl.desolatesky.breaking.listener.BreakingListener;
import com.fisherl.desolatesky.entity.EntityFactory;
import com.fisherl.desolatesky.island.Island;
import com.fisherl.desolatesky.item.ItemFactory;
import com.fisherl.desolatesky.loot.LootFactory;
import com.fisherl.desolatesky.player.DSPlayer;
import com.fisherl.desolatesky.recipe.RecipeFactory;
import com.fisherl.desolatesky.util.Constants;
import com.fisherl.desolatesky.util.DistanceUtil;
import com.fisherl.desolatesky.util.chance.Chance;
import com.fisherl.desolatesky.world.region.Region;
import com.fisherl.desolatesky.world.region.SquareRegion;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Area;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.WorldBorder;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.network.packet.server.play.WorldBorderSizePacket;
import net.minestom.server.registry.RegistryKey;
import net.minestom.server.utils.Direction;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.SequencedSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.random.RandomGenerator;

public class IslandWorld extends InstanceContainer implements DSWorld {


    public static final Point DEFAULT_SPAWN_POINT = new Pos(0.5, Constants.WORLD_MIN_Y + 2, 0.5);
    public static final SquareRegion STARTING_REGION = Region.square(DEFAULT_SPAWN_POINT.asBlockVec(), 7, Constants.WORLD_MIN_Y, Constants.WORLD_MAX_Y);
    public static final SquareRegion MAX_REGION_SIZE = Region.square(DEFAULT_SPAWN_POINT.asBlockVec(), Constants.MAX_WORLD_RADIUS, Constants.WORLD_MIN_Y, Constants.WORLD_MAX_Y);

    private final RandomGenerator randomGenerator;
    private final BlockFactory blockFactory;
    private final ItemFactory itemFactory;
    private final EntityFactory entityFactory;
    private final LootFactory lootFactory;
    private final RecipeFactory recipeFactory;
    private final Map<Long, SequencedSet<Point>> scheduledBlockUpdates = new ConcurrentHashMap<>();
    private final ConfiguredBreakingManager breakingManager;
    private final Island island;
    private long tickTime = 0;

    public IslandWorld(RandomGenerator randomGenerator,
                       BlockFactory blockFactory,
                       ItemFactory itemFactory,
                       EntityFactory entityFactory,
                       LootFactory lootFactory,
                       RecipeFactory recipeFactory,
                       UUID uuid,
                       RegistryKey<DimensionType> dimensionType,
                       Island island,
                       Path worldFolder) {
        super(uuid, dimensionType);
        this.randomGenerator = randomGenerator;
        this.blockFactory = blockFactory;
        this.itemFactory = itemFactory;
        this.entityFactory = entityFactory;
        this.lootFactory = lootFactory;
        this.recipeFactory = recipeFactory;
        this.island = island;
        this.breakingManager = new ConfiguredBreakingManager(this, this.blockFactory);
        this.setGenerator(new PlayerWorldGenerator(blockFactory));
        this.setChunkSupplier(LightingChunk::new);
        this.setChunkLoader(new AnvilLoader(worldFolder));
        final SquareRegion region = this.island.worldSize();
        final Point center = region.center();
        this.setWorldBorder(new WorldBorder(region.radius() * 2 + 2, center.x() + 0.5, center.z() + 0.5, 0, 0, (int) region.radius()  * 2 + 2));
        this.initialize();
    }

    @Override
    public final void tick(long time) {
        super.tick(time);
        this.tickTime++;
        this.randomTick(this.tickTime);
        this.doBlockUpdates(this.tickTime);
        this.breakingManager.tick();
    }

    @Override
    public void sendWorldBorder(Player player) {
        player.sendPacket(new WorldBorderSizePacket(this.island.worldSize().radius() * 2 + 1));
    }

    private void doBlockUpdates(long time) {
        final SequencedSet<Point> scheduledUpdates = this.scheduledBlockUpdates.remove(time);
        if (scheduledUpdates == null || scheduledUpdates.isEmpty()) {
            return;
        }
        scheduledUpdates.forEach(point -> {
            if (!this.isChunkLoaded(point)) {
                return;
            }
            final Block block = this.getBlock(point);
            final BlockSetting.Result result = this.blockFactory.getBlockDefinition(block)
                    .stream()
                    .flatMap(definition -> definition.settings().getAllSettings().stream())
                    .map(setting -> setting.checkState(this, point, block))
                    .reduce(BlockSetting.Result.GOOD, (f, s) -> {
                        if (f.ordinal() < s.ordinal()) {
                            return f;
                        }
                        return s;
                    });
            switch (result) {
                case DESTROY_AND_DROP -> this.destroyAndDropBlock(point, block);
                case DESTROY -> this.destroyAndDropBlock(point, block);
                case GOOD -> this.blockFactory.getBlockDefinition(block)
                        .flatMap(b -> b.getBehavior(BlockBehavior.Type.UPDATE))
                        .filter(behavior -> behavior.update(this, point, block))
                        .stream()
                        .flatMap(behavior -> behavior.getBlocksToUpdate(this, point, block).stream())
                        .distinct()
                        .forEach(this::scheduleUpdate);
            }
        });
    }

    @Override
    public void setBlock(int x, int y, int z, @NotNull Block block, boolean doBlockUpdates) {
        super.setBlock(x, y, z, block, false);
        if (doBlockUpdates) {
            this.scheduleNeighborUpdates(new BlockVec(x, y, z));
        }
    }

    @Override
    public boolean placeBlock(@NotNull BlockHandler.Placement placement, boolean doBlockUpdates) {
        final boolean placed = super.placeBlock(placement, false);
        if (!placed) {
            return false;
        }
        if (!doBlockUpdates) {
            return true;
        }
        this.scheduleNeighborUpdates(placement.getBlockPosition());
        return true;
    }

    @Override
    public boolean breakBlock(@NotNull Player player, @NotNull Point blockPosition, @NotNull BlockFace blockFace, boolean doBlockUpdates) {
        final boolean broken = super.breakBlock(player, blockPosition, blockFace, false);
        if (!broken) {
            return false;
        }
        if (!doBlockUpdates) {
            return true;
        }
        this.scheduleNeighborUpdates(blockPosition);
        return true;
    }

    @Override
    public void setBlock(int x, int y, int z, @NotNull Block block) {
        this.setBlock(x, y, z, block, true);
    }

    @Override
    public void setBlock(@NotNull Point blockPosition, @NotNull Block block, boolean doBlockUpdates) {
        super.setBlock(blockPosition, block, false);
        if (doBlockUpdates) {
            this.scheduleNeighborUpdates(blockPosition);
        }
    }

    @Override
    public boolean placeBlock(@NotNull BlockHandler.Placement placement) {
        return this.placeBlock(placement, true);
    }

    @Override
    public boolean breakBlock(@NotNull Player player, @NotNull Point blockPosition, @NotNull BlockFace blockFace) {
        return this.breakBlock(player, blockPosition, blockFace, true);
    }

    @Override
    public void setBlock(@NotNull Point blockPosition, @NotNull Block block) {
        this.setBlock(blockPosition, block, true);
    }

    @Override
    public void setBlockArea(@NotNull Area area, @NotNull Block block) {
        super.setBlockArea(area, block);
    }

    private void destroyAndDropBlock(Point point, Block block) {
        this.setBlock(point, Block.AIR, true);
    }

    public void scheduleUpdate(Point point) {
        this.scheduledBlockUpdates.computeIfAbsent(this.tickTime + 1, _ -> new LinkedHashSet<>()).add(point);
    }

    public void scheduleNeighborUpdates(Point point) {
        this.scheduleUpdate(point.add(Direction.UP.vec()));
        this.scheduleUpdate(point.add(Direction.NORTH.vec()));
        this.scheduleUpdate(point.add(Direction.SOUTH.vec()));
        this.scheduleUpdate(point.add(Direction.EAST.vec()));
        this.scheduleUpdate(point.add(Direction.WEST.vec()));
        this.scheduleUpdate(point.add(Direction.DOWN.vec()));
    }

    @Override
    public UUID worldId() {
        return this.getUuid();
    }

    @Override
    public boolean canBreakBlock(DSPlayer player, Point blockPosition, Block block) {
        return true;
    }

    @Override
    public ConfiguredBreakingManager breakingManager() {
        return this.breakingManager;
    }

    private static final int TEMP_BLOCK_COUNT = 3;

    private void randomTick(long time) {
        this.getPlayers()
                .stream()
                .flatMap(player -> DistanceUtil.getChunksNear(this, player.getPosition(), 5).stream())
                .distinct()
                .forEach(this::randomTickChunk);
    }


    private void randomTickChunk(Chunk chunk) {
        for (int sectionIndex = chunk.getMinSection(); sectionIndex < chunk.getMaxSection(); sectionIndex++) {
            for (int i = 0; i < TEMP_BLOCK_COUNT; i++) {
                final int randX = this.randomGenerator.nextInt(0, Chunk.CHUNK_SIZE_X);
                final int randY = this.randomGenerator.nextInt(0, Chunk.CHUNK_SECTION_SIZE);
                final int randZ = this.randomGenerator.nextInt(0, Chunk.CHUNK_SIZE_Z);
                final Point pos = new BlockVec(chunk.getChunkX() * Chunk.CHUNK_SIZE_X + randX,
                        sectionIndex * Chunk.CHUNK_SECTION_SIZE + randY,
                        chunk.getChunkZ() * Chunk.CHUNK_SIZE_Z + randZ);
                final Block block = chunk.getBlock(pos, Condition.TYPE);
                if (block.isAir()) {
                    continue;
                }
                this.blockFactory.getBlockDefinition(chunk.getBlock(pos))
                        .stream()
                        .flatMap(b -> b.getBehavior(BlockBehavior.Type.RANDOM_TICK).stream())
                        .forEach(behavior -> behavior.onRandomTick(this, pos, block, this.blockFactory.getBlockId(block)));
            }
        }
    }

    @Override
    public void setBlock(Point pos, Key blockId, Function<Block, Block> blockTransformer) {
        this.blockFactory.getBlockDefinition(blockId)
                .map(BlockDefinition::defaultBlock)
                .map(blockTransformer)
                .ifPresent(block -> this.setBlock(pos, block, true));
    }

    @Override
    public RandomGenerator getRandomGenerator(Point pos) {
        return this.randomGenerator;
    }

    @Override
    public boolean rollChance(Point pos, double chance) {
        return Chance.roll(this.randomGenerator, chance);
    }

    private void initialize() {
        new BreakingListener().register(this.eventNode());
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

    public Island island() {
        return this.island;
    }
}
