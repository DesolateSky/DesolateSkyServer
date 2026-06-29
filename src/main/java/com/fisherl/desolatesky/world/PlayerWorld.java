package com.fisherl.desolatesky.world;

import com.fisherl.desolatesky.block.BlockFactory;
import com.fisherl.desolatesky.block.behavior.BlockBehavior;
import com.fisherl.desolatesky.block.definition.BlockDefinition;
import com.fisherl.desolatesky.block.setting.BlockSetting;
import com.fisherl.desolatesky.breaking.BreakingManager;
import com.fisherl.desolatesky.breaking.listener.BreakingListener;
import com.fisherl.desolatesky.player.DSPlayer;
import com.fisherl.desolatesky.util.chance.Chance;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Area;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.registry.RegistryKey;
import net.minestom.server.utils.Direction;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.SequencedSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.random.RandomGenerator;

public class PlayerWorld extends InstanceContainer implements DSWorld {

    public static final Point DEFAULT_SPAWN_POINT = new Pos(0.5, 65, 0.5);

    private final RandomGenerator randomGenerator;
    private final BlockFactory blockFactory;
    private final Map<Long, SequencedSet<Point>> scheduledBlockUpdates = new ConcurrentHashMap<>();
    private final BreakingManager breakingManager;
    private long tickTime = 0;

    public PlayerWorld(RandomGenerator randomGenerator, BlockFactory blockFactory, UUID uuid, RegistryKey<DimensionType> dimensionType) {
        super(uuid, dimensionType);
        this.randomGenerator = randomGenerator;
        this.blockFactory = blockFactory;
        this.breakingManager = new BreakingManager(this, this.blockFactory);
        this.setGenerator(new PlayerWorldGenerator(blockFactory));
        this.setChunkSupplier(LightingChunk::new);

        this.initialize();
    }

//    public PlayerWorld(RandomGenerator randomGenerator, UUID uuid, RegistryKey<DimensionType> dimensionType, Key dimensionName) {
//        this.randomGenerator = randomGenerator;
//        super(uuid, dimensionType, dimensionName);
//    }
//
//    public PlayerWorld(RandomGenerator randomGenerator, UUID uuid, RegistryKey<DimensionType> dimensionType, @Nullable ChunkLoader loader) {
//        super(uuid, dimensionType, loader);
//        this.randomGenerator = randomGenerator;
//    }
//
//    public PlayerWorld(RandomGenerator randomGenerator, UUID uuid, RegistryKey<DimensionType> dimensionType, @Nullable ChunkLoader loader, Key dimensionName) {
//        super(uuid, dimensionType, loader, dimensionName);
//        this.randomGenerator = randomGenerator;
//    }
//
//    public PlayerWorld(RandomGenerator randomGenerator, DynamicRegistry<DimensionType> dimensionTypeRegistry, UUID uuid, RegistryKey<DimensionType> dimensionType, @Nullable ChunkLoader loader, Key dimensionName) {
//        super(dimensionTypeRegistry, uuid, dimensionType, loader, dimensionName);
//        this.randomGenerator = randomGenerator;
//    }

    @Override
    public final void tick(long time) {
        super.tick(time);
        this.tickTime++;
        this.randomTick(this.tickTime);
        this.doBlockUpdates(this.tickTime);
        this.breakingManager.tick();
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
    public BreakingManager breakingManager() {
        return this.breakingManager;
    }

    private static final int TEMP_BLOCK_COUNT = 3;

    private void randomTick(long time) {
        this.getChunks().forEach(this::randomTickChunk);
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
                final Block block = this.getBlock(pos);
                this.blockFactory.getBlockDefinition(block)
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
}
