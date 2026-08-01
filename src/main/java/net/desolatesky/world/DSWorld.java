package net.desolatesky.world;

import net.desolatesky.block.BlockFactory;
import net.desolatesky.block.behavior.BlockBehavior;
import net.desolatesky.block.behavior.BlockDropBehavior;
import net.desolatesky.block.behavior.BlockUpdateBehavior;
import net.desolatesky.block.behavior.PlaceRequirementsBehavior;
import net.desolatesky.block.behavior.RandomTickBehavior;
import net.desolatesky.block.behavior.listener.LoadBehavior;
import net.desolatesky.block.definition.BlockDefinition;
import net.desolatesky.breaking.BreakingManager;
import net.desolatesky.breaking.listener.BreakingListener;
import net.desolatesky.entity.EntityManager;
import net.desolatesky.item.ItemFactory;
import net.desolatesky.loot.LootFactory;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.recipe.RecipeFactory;
import net.desolatesky.util.BlockUtil;
import net.desolatesky.util.DistanceUtil;
import net.desolatesky.util.MinecraftUtil;
import net.desolatesky.util.RegionUtil;
import net.desolatesky.util.chance.Chance;
import net.desolatesky.world.region.SquareRegion;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Area;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.CoordConversion;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.instance.generator.Generator;
import net.minestom.server.item.ItemStack;
import net.minestom.server.registry.RegistryKey;
import net.minestom.server.utils.Direction;
import net.minestom.server.utils.chunk.ChunkUtils;
import net.minestom.server.utils.position.PositionUtils;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.SequencedSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract sealed class DSWorld extends InstanceContainer permits PlayerWorld, LobbyWorld, VoidWorld {

    protected final RandomGenerator randomGenerator;
    protected final BlockFactory blockFactory;
    protected final ItemFactory itemFactory;
    protected final EntityManager entityFactory;
    protected final LootFactory lootFactory;
    protected final RecipeFactory recipeFactory;
    protected final Map<Long, SequencedSet<Point>> scheduledBlockUpdates = new ConcurrentHashMap<>();
    protected final BreakingManager breakingManager;
    protected final int randomTickCount;
    protected final Path worldFolder;
    protected long tickTime = 0;

    public DSWorld(
            Generator generator,
            RandomGenerator randomGenerator,
            BlockFactory blockFactory,
            ItemFactory itemFactory,
            EntityManager entityFactory,
            LootFactory lootFactory,
            RecipeFactory recipeFactory,
            BreakingManager breakingManager,
            UUID uuid,
            RegistryKey<DimensionType> dimensionType,
            Path worldFolder,
            int randomTickCount
    ) {
        super(uuid, dimensionType);
        this.randomGenerator = randomGenerator;
        this.blockFactory = blockFactory;
        this.itemFactory = itemFactory;
        this.entityFactory = entityFactory;
        this.lootFactory = lootFactory;
        this.recipeFactory = recipeFactory;
        this.breakingManager = breakingManager;
        this.randomTickCount = randomTickCount;
        this.worldFolder = worldFolder;
        this.migrateWorld(this.worldFolder);
        this.setGenerator(generator);
        this.setChunkSupplier(LightingChunk::new);
        this.setChunkLoader(new AnvilLoader(worldFolder, this.getDimensionType().key()));
//        this.setChunkLoader(new AnvilLoader(worldFolder));
        this.enableAutoChunkLoad(true);
        this.initialize();
    }

    private void migrateWorld(Path worldFolder) {
        if (!Files.exists(worldFolder)) {
            return;
        }
        if (Files.exists(worldFolder.resolve("dimensions"))) {
            return;
        }
        final Path newPath = worldFolder.resolve("dimensions")
                .resolve(this.getDimensionType().key().namespace())
                .resolve(this.getDimensionType().key().value())
                .resolve("region");
        try (final Stream<Path> stream = Files.walk(worldFolder)) {
            Files.createDirectories(newPath);
            stream.filter(Files::isRegularFile)
                    .forEach(p -> {
                        try {
                            Files.move(p, newPath.resolve(p.getFileName()), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public final void tick(long time) {
        super.tick(time);
        this.tickTime++;
        this.randomTick(this.tickTime);
        this.doBlockUpdates(this.tickTime);
        this.breakingManager.tick(this);
    }

    public abstract void sendWorldBorder(Player player);

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
            final BlockDefinition blockDefinition = this.blockFactory.getBlockDefinition(block);
            if (blockDefinition == null) {
                return;
            }
            final PlaceRequirementsBehavior placeRequirementsBehavior = blockDefinition.getBehavior(BlockBehavior.Type.PLACE_REQUIREMENTS);
            final PlaceRequirementsBehavior.Result result = placeRequirementsBehavior == null ? PlaceRequirementsBehavior.Result.GOOD : placeRequirementsBehavior.checkState(this, point, block);
            switch (result) {
                case DESTROY_AND_DROP -> this.destroyAndDropBlock(point, block);
                case DESTROY -> this.destroyBlock(point);
                case GOOD -> {
                    final BlockUpdateBehavior updateBehavior = blockDefinition.getBehavior(BlockBehavior.Type.UPDATE);
                    if (updateBehavior == null) {
                        return;
                    }
                    if (!updateBehavior.update(this, point, block)) {
                        return;
                    }
                    updateBehavior.getBlocksToUpdate(this, point, block)
                            .forEach(this::scheduleUpdate);
                }
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
        final BlockDefinition blockDefinition = this.blockFactory.getBlockDefinition(block);
        if (blockDefinition == null) {
            this.destroyBlock(point);
            return;
        }
        final BlockDropBehavior blockDropBehavior = blockDefinition.getBehavior(BlockBehavior.Type.BLOCK_DROP);
        if (blockDropBehavior == null) {
            this.destroyBlock(point);
            return;
        }
        final Collection<ItemStack> drops = blockDropBehavior.getDrops(this, point, block, BlockUtil.getBlockId(block), this.itemFactory, null);
        this.destroyBlock(point);
        MinecraftUtil.spawnDroppedBlockItems(this, point, drops);
    }

    private void destroyBlock(Point point) {
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

    public abstract boolean canBreakBlock(DSPlayer player, Point blockPosition, Block block);

    public abstract boolean canPlaceBlock(DSPlayer player, Point blockPosition, Block block);

    public void breakBlock(DSPlayer player, Point blockPosition) {
        final Block blockType = this.getBlock(blockPosition, Condition.TYPE);
        if (blockType == null || blockType.air()) {
            return;
        }
        final Block block = this.getBlock(blockPosition);
        final BlockDefinition blockDefinition = this.blockFactory.getBlockDefinition(block);
        if (blockDefinition == null) {
            return;
        }
        final BlockDropBehavior dropBehavior = blockDefinition.getBehavior(BlockBehavior.Type.BLOCK_DROP);
        this.setBlock(blockPosition, Block.AIR);
        if (dropBehavior == null) {
            return;
        }
        final Collection<ItemStack> drops =  dropBehavior.getDrops(this,
                blockPosition,
                block,
                BlockUtil.getBlockId(block),
                this.itemFactory,
                player.getItemInMainHand()
        );
        MinecraftUtil.spawnDroppedBlockItems(this, blockPosition, drops);
    }

    public BreakingManager breakingManager() {
        return this.breakingManager;
    }

    private void randomTick(long time) {
        if (this.randomTickCount <= 0) {
            return;
        }
        final Set<Chunk> chunks = this.getPlayers()
                .stream()
                .flatMap(player -> DistanceUtil.getChunksNear(this, player.getPosition(), 5).stream())
                .collect(Collectors.toSet());
        chunks.addAll(DistanceUtil.getChunksNear(this, new Vec(0), 5));
        chunks.forEach(this::randomTickChunk);
    }


    private void randomTickChunk(Chunk chunk) {
        for (int sectionIndex = chunk.getMinSection(); sectionIndex < chunk.getMaxSection(); sectionIndex++) {
            for (int i = 0; i < this.randomTickCount; i++) {
                final int randX = this.randomGenerator.nextInt(0, Chunk.CHUNK_SIZE_X);
                final int randY = this.randomGenerator.nextInt(0, Chunk.CHUNK_SECTION_SIZE);
                final int randZ = this.randomGenerator.nextInt(0, Chunk.CHUNK_SIZE_Z);
                final Point pos = new BlockVec(chunk.getChunkX() * Chunk.CHUNK_SIZE_X + randX,
                        sectionIndex * Chunk.CHUNK_SECTION_SIZE + randY,
                        chunk.getChunkZ() * Chunk.CHUNK_SIZE_Z + randZ);
                final Block blockType = chunk.getBlock(pos, Condition.TYPE);
                if (blockType == null || blockType.air()) {
                    continue;
                }
                final Block block = chunk.getBlock(pos);
                final BlockDefinition blockDefinition = this.blockFactory.getBlockDefinition(block);
                if (blockDefinition == null) {
                    continue;
                }
                final RandomTickBehavior tickBehavior = blockDefinition.getBehavior(BlockBehavior.Type.RANDOM_TICK);
                if (tickBehavior == null) {
                    continue;
                }
                tickBehavior.onRandomTick(this, pos, block, this.blockFactory.getBlockId(block));
            }
        }
    }

    public void setBlock(Point pos, Key blockId, Function<Block, Block> blockTransformer) {
        final BlockDefinition blockDefinition = this.blockFactory.getBlockDefinition(blockId);
        if (blockDefinition == null) {
            return;
        }
        final Block block = blockTransformer.apply(blockDefinition.createBlock());
        this.setBlock(pos, block, true);
    }

    public RandomGenerator getRandomGenerator(Point pos) {
        return this.randomGenerator;
    }

    public boolean rollChance(Point pos, double chance) {
        return Chance.roll(this.randomGenerator, chance);
    }

    private void initialize() {
        new BreakingListener().register(this.eventNode());
    }

    public abstract Point getSpawnPointFor(DSPlayer player);

    public abstract SquareRegion getRegion();

    public Instance asInstance() {
        return this;
    }

    public BlockFactory blockFactory() {
        return this.blockFactory;
    }

    public ItemFactory itemFactory() {
        return this.itemFactory;
    }

    public EntityManager entityFactory() {
        return this.entityFactory;
    }

    public LootFactory lootFactory() {
        return this.lootFactory;
    }

    public RecipeFactory recipeFactory() {
        return this.recipeFactory;
    }

    public abstract WorldType worldType();

    public CompletableFuture<Void> save() {
        for (final Chunk chunk : this.getChunks()) {
            if (!chunk.isLoaded()) {
                continue;
            }
            chunk.getBlockEntities().forEach((pos, block) -> {
                final BlockDefinition blockDefinition = this.blockFactory.getBlockDefinition(block);
                if (blockDefinition == null) {
                    return;
                }
                final LoadBehavior loadBehavior = blockDefinition.getBehavior(BlockBehavior.Type.LOAD);
                if (loadBehavior == null) {
                    return;
                }
                final Point worldPos = CoordConversion.chunkBlockRelativeGetGlobal(pos.blockX(), pos.blockY(), pos.blockZ(), chunk.getChunkX(), chunk.getChunkZ());
                loadBehavior.save(this, worldPos, block);
            });
        }
        return this.saveInstance().thenCompose(_ -> this.saveChunksToStorage());
    }

    public Path worldFolder() {
        return this.worldFolder;
    }
}
