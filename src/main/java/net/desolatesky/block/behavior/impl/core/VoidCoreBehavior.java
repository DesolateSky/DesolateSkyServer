package net.desolatesky.block.behavior.impl.core;

import net.desolatesky.block.BlockIds;
import net.desolatesky.block.BlockTags;
import net.desolatesky.block.behavior.ClickBehavior;
import net.desolatesky.block.behavior.TickBehavior;
import net.desolatesky.block.behavior.impl.BlockEntityBehavior;
import net.desolatesky.block.behavior.listener.LoadBehavior;
import net.desolatesky.block.behavior.serializer.BlockBehaviorSerializer;
import net.desolatesky.block.definition.BlockDefinition;
import net.desolatesky.block.handler.DSBlockHandler;
import net.desolatesky.entity.EntityIds;
import net.desolatesky.entity.EntityManager;
import net.desolatesky.entity.EntityTags;
import net.desolatesky.entity.IslandEntity;
import net.desolatesky.island.permission.IslandPermission;
import net.desolatesky.item.ItemFactory;
import net.desolatesky.item.ItemTags;
import net.desolatesky.item.definition.ItemDefinition;
import net.desolatesky.loot.LootFactory;
import net.desolatesky.loot.LootTable;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.Namespace;
import net.desolatesky.world.DSWorld;
import net.desolatesky.world.VoidWorld;
import net.desolatesky.world.region.Region;
import net.desolatesky.world.region.SquareRegion;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.Weather;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.item.ItemStack;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.random.RandomGenerator;

public final class VoidCoreBehavior implements ClickBehavior, TickBehavior, LoadBehavior, BlockEntityBehavior {

    private static final Key KEY = Namespace.key("void_core");

    public static final class Serializer extends BlockBehaviorSerializer<VoidCoreBehavior> {

        public Serializer() {
            super(KEY);
        }

        private static final String GENERATE_ITEM_CHANCE_KEY = "generate-item-chance";

        @Override
        public VoidCoreBehavior deserialize(java.lang.reflect.Type type, ConfigurationNode node) throws SerializationException {
            final double generateItemChance = node.node(GENERATE_ITEM_CHANCE_KEY).getDouble();
            return new VoidCoreBehavior(generateItemChance);
        }

        @Override
        public void serialize(java.lang.reflect.Type type, @org.jspecify.annotations.Nullable VoidCoreBehavior obj, ConfigurationNode node) throws SerializationException {
            if (obj == null) {
                return;
            }
            node.node(GENERATE_ITEM_CHANCE_KEY, obj.generateItemChance);
        }

        @Override
        public Class<VoidCoreBehavior> behaviorClass() {
            return VoidCoreBehavior.class;
        }
    }

    private static final BlockHandler BLOCK_HANDLER = DSBlockHandler.newTickingBlockHandler(KEY);
    private static final Region BLOCK_REGION = Region.square(new Vec(0.5, 0, 0.5), 0.5, 0, 1);

    private final double generateItemChance;

    public VoidCoreBehavior(double generateItemChance) {
        this.generateItemChance = generateItemChance;
    }

    @Override
    public BlockHandler createBlockHandler() {
        return BLOCK_HANDLER;
    }

    @Override
    public ClickBehavior.Result onRightClick(DSWorld world, DSPlayer player, PlayerHand hand, Point clickedPos, Block clickedBlock, ItemStack clickedWith) {
        if (!(world instanceof final VoidWorld voidWorld)) {
            return Result.ALLOW;
        }
        final Key islandCoreSpawnerKey = clickedWith.getTag(ItemTags.ISLAND_CORE_SPAWNER_KEY);
        if (islandCoreSpawnerKey == null) {
            return Result.ALLOW;
        }
        final IslandCoreMobSpawner spawner = IslandCoreMobSpawner.SPAWNERS.get(islandCoreSpawnerKey);
        if (spawner == null) {
            return Result.ALLOW;
        }
        if (!voidWorld.island().hasPermission(player.getUuid(), IslandPermission.INTERACT_VOID_CORE)) {
            return Result.BLOCK_INTERACTION;
        }
        if (clickedBlock.getTag(ItemTags.ISLAND_CORE_SPAWNER_KEY) != null) {
            return Result.BLOCK_INTERACTION;
        }
        final EntityManager entityFactory = world.entityFactory();
        entityFactory.createEntity(EntityIds.ISLAND_CORE_SPAWNER_DISPLAY, voidWorld.island(), e -> {
            e.setTag(EntityTags.ITEM_DISPLAY_KEY, spawner.itemDisplayKey());
            e.setInstance(world.asInstance(), clickedPos.asBlockVec().add(0.5, 1.5, 0.5));
            world.setBlock(clickedPos, clickedBlock.withTag(BlockTags.ISLAND_CORE_SPAWNER_KEY, islandCoreSpawnerKey)
                    .withTag(BlockTags.ISLAND_CORE_DISPLAY_ENTITY_ID, e.getUuid()));
            player.setItemInHand(hand, clickedWith.withAmount(clickedWith.amount() - 1));
        });
        return Result.BLOCK_INTERACTION;
    }

    @Override
    public Result onLeftClick(DSWorld world, DSPlayer player, Point clickedPos, Block clickedBlock, ItemStack clickedWith) {
        if (!(world instanceof final VoidWorld voidWorld)) {
            return Result.ALLOW;
        }
        final Key islandCoreSpawnerKey = clickedBlock.getTag(BlockTags.ISLAND_CORE_SPAWNER_KEY);
        if (islandCoreSpawnerKey == null) {
            return Result.ALLOW;
        }
        final IslandCoreMobSpawner spawner = IslandCoreMobSpawner.SPAWNERS.get(islandCoreSpawnerKey);
        if (spawner == null) {
            return Result.ALLOW;
        }
        if (!voidWorld.island().hasPermission(player.getUuid(), IslandPermission.INTERACT_VOID_CORE)) {
            return Result.BLOCK_INTERACTION;
        }
        final ItemDefinition itemDefinition = world.itemFactory().getItemDefinition(spawner.itemDisplayKey());
        if (itemDefinition == null) {
            return Result.BLOCK_INTERACTION;
        }
        final BlockDefinition blockDefinition = world.blockFactory().getBlockDefinition(BlockIds.VOID_CORE);
        if (blockDefinition == null) {
            return Result.BLOCK_INTERACTION;
        }
        final ItemStack itemStack = itemDefinition.defaultItemStack();
        this.removeSpawner(world, clickedPos, clickedBlock);
        player.getInventory().addItemStack(itemStack);
        world.setBlock(clickedPos, clickedBlock.withTag(BlockTags.ISLAND_CORE_SPAWNER_KEY, null));
        return Result.ALLOW;
    }

    public @Nullable ItemStack removeSpawner(DSWorld world, Point pos, Block block) {
        final UUID entityId = block.getTag(BlockTags.ISLAND_CORE_DISPLAY_ENTITY_ID);
        final Entity entity = world.asInstance().getEntityByUuid(entityId);
        if (entity != null) {
            entity.remove();
        }
        world.setBlock(pos, block.withTag(BlockTags.ISLAND_CORE_SPAWNER_KEY, null)
                .withTag(BlockTags.ISLAND_CORE_DISPLAY_ENTITY_ID, null));
        final Key islandCoreSpawnerKey = block.getTag(BlockTags.ISLAND_CORE_SPAWNER_KEY);
        if (islandCoreSpawnerKey == null) {
            return null;
        }
        final IslandCoreMobSpawner spawner = IslandCoreMobSpawner.SPAWNERS.get(islandCoreSpawnerKey);
        if (spawner == null) {
            return null;
        }
        final ItemDefinition itemDefinition = world.itemFactory().getItemDefinition(spawner.itemDisplayKey());
        if (itemDefinition == null) {
            return null;
        }
        return itemDefinition.defaultItemStack();
    }

    public boolean hasSpawner(DSWorld world, Point pos, Block block) {
        return block.hasTag(BlockTags.ISLAND_CORE_SPAWNER_KEY);
    }

    @Override
    public void onTick(DSWorld world, Point pos, Block block, Key blockId) {
        if (!isStormActive(world, pos, block, blockId)) {
            if (world.rollChance(pos, this.generateItemChance)) {
                spawnEntity(world, pos, block, blockId);
            }
            attemptCatalyst(world, pos, block, blockId);
            return;
        }
        if (!world.rollChance(pos, this.generateItemChance)) {
            return;
        }

        final ItemFactory itemFactory = world.itemFactory();
        final LootFactory lootFactory = world.lootFactory();

        final LootTable lootTable = lootFactory.getLootTable(block.getTag(BlockTags.ISLAND_CORE_STORM_LOOT_TABLE)).orElse(null);
        if (lootTable == null) {
            return;
        }

        final RandomGenerator randomGenerator = world.getRandomGenerator(pos);

        final List<ItemStack> loot = lootTable.roll(randomGenerator, itemFactory);

        final Point randPos = getRandomSpawnPoint(world, pos, randomGenerator);
        final Block randBlock = world.getBlock(randPos, Block.Getter.Condition.TYPE);
        if (randBlock == null || !randBlock.air()) {
            return;
        }

        final Instance instanceContainer = world.asInstance();

        for (final ItemStack itemStack : loot) {
            final ItemEntity item = new ItemEntity(itemStack);
            item.setInstance(instanceContainer, randPos);
        }
        final Entity lightning = new Entity(EntityType.LIGHTNING_BOLT);
        lightning.setInstance(instanceContainer, randPos);
    }

    private static void spawnEntity(DSWorld world, Point pos, Block block, Key blockId) {
        if (!(world instanceof final VoidWorld voidWorld)) {
            return;
        }
        final Key spawnerKey = block.getTag(BlockTags.ISLAND_CORE_SPAWNER_KEY);
        if (spawnerKey == null) {
            return;
        }
        final IslandCoreMobSpawner spawner = IslandCoreMobSpawner.SPAWNERS.get(spawnerKey);
        if (spawner == null) {
            return;
        }
        final Instance instance = world.asInstance();
        if (instance.getEntities().stream().filter(e -> e.hasTag(EntityTags.ISLAND_CORE_MOB)).count() > 5) {
            return;
        }

        final RandomGenerator randomGenerator = world.getRandomGenerator(pos);
        final Key entityType = spawner.rollEntity(randomGenerator);
        final EntityManager entityFactory = world.entityFactory();
        final IslandEntity islandEntity = entityFactory.createEntity(entityType, voidWorld.island(), e -> {

        });
        if (islandEntity != null) {
            final Point randPos = getRandomEntitySpawnPoint(world, pos, world.getRandomGenerator(pos));
            final Entity entity = islandEntity.asEntity();
            entity.setTag(EntityTags.ISLAND_CORE_MOB, true);
            entity.setInstance(instance, randPos);
        }
    }

    private static Point getRandomSpawnPoint(DSWorld world, Point pos, RandomGenerator randomGenerator) {
        final SquareRegion region = world.getRegion();
        final double radius = region.radius() - 3;
        final double randX = randomGenerator.nextDouble(-radius, radius);
        final double randZ = randomGenerator.nextDouble(-radius, radius);
        return pos.add(randX, 0, randZ);
    }

    private static Point getRandomEntitySpawnPoint(DSWorld world, Point pos, RandomGenerator randomGenerator) {
        final SquareRegion region = world.getRegion();
        final double radius = region.radius() - 0.5;
        final int edge = randomGenerator.nextInt(0, 4);
        final double randCoord = randomGenerator.nextDouble(-radius, radius + 1);
        return switch (edge) {
            case 0 -> pos.add(randCoord, 0, radius);
            case 1 -> pos.add(randCoord, 0, -radius);
            case 2 -> pos.add(radius, 0, randCoord);
            case 3 -> pos.add(-radius, 0, randCoord);
            default -> pos.add(radius, 0, radius);
        };
    }

    private static boolean isStormActive(DSWorld world, Point pos, Block block, Key blockId) {
        if (!block.hasTag(BlockTags.ISLAND_CORE_STORM_ACTIVE) || !block.getTag(BlockTags.ISLAND_CORE_STORM_ACTIVE)) {
            return false;
        }
        if (!block.hasTag(BlockTags.ISLAND_CORE_STORM_LOOT_TABLE) || block.getTag(BlockTags.ISLAND_CORE_STORM_LOOT_TABLE) == null) {
            return false;
        }
        final Instant end = block.getTag(BlockTags.ISLAND_CORE_STORM_END);
        if (end == null) {
            return false;
        }
        if (end.isBefore(Instant.now())) {
            world.setBlock(pos, block.withTag(BlockTags.ISLAND_CORE_STORM_END, null));
            world.asInstance().setWeather(Weather.CLEAR);
        }
        return true;
    }

    private static void attemptCatalyst(DSWorld world, Point pos, Block block, Key blockId) {
        final Instance instance = world.asInstance();
        if (!instance.isChunkLoaded(pos)) {
            return;
        }
        final Chunk chunk = instance.getChunkAt(pos);
        if (chunk == null) {
            return;
        }
        instance.getChunkEntities(chunk).stream()
                .filter(e -> isValidEntity(e, pos, block))
                .forEach(entity -> {
                    final ItemEntity item = (ItemEntity) entity;
                    final ItemStack itemStack = item.getItemStack();
                    final Key lootKey = itemStack.getTag(ItemTags.ISLAND_CORE_STORM_LOOT);
                    item.remove();
                    final Duration duration = itemStack.getTag(ItemTags.ISLAND_CORE_STORM_DURATION);
                    world.setBlock(pos, block.withTag(BlockTags.ISLAND_CORE_STORM_ACTIVE, true)
                            .withTag(BlockTags.ISLAND_CORE_STORM_END, Instant.now().plus(duration))
                            .withTag(BlockTags.ISLAND_CORE_STORM_LOOT_TABLE, lootKey));
                    world.asInstance().setWeather(Weather.THUNDER);
                });
    }

    private static boolean isValidEntity(Entity entity, Point blockPos, Block block) {
        if (!(entity instanceof final ItemEntity item)) {
            return false;
        }
        final Point entityPos = entity.getPosition().sub(blockPos);
        if (!BLOCK_REGION.contains(entityPos)) {
            return false;
        }
        if (entityPos.y() < blockPos.y()) {
            return false;
        }
        final ItemStack itemStack = item.getItemStack();
        return itemStack.hasTag(ItemTags.ISLAND_CORE_STORM_LOOT) && itemStack.hasTag(ItemTags.ISLAND_CORE_STORM_DURATION);
    }

    @Override
    public void save(DSWorld world, Point blockPos, Block block) {

    }

    @Override
    public void onLoad(DSWorld world, Point blockPos, Block block) {
        if (!(world instanceof final VoidWorld voidWorld)) {
            return;
        }
        final EntityManager entityFactory = world.entityFactory();
        final Key islandCoreSpawnerKey = block.getTag(BlockTags.ISLAND_CORE_SPAWNER_KEY);
        if (islandCoreSpawnerKey == null) {
            return;
        }
        final IslandCoreMobSpawner spawner = IslandCoreMobSpawner.SPAWNERS.get(islandCoreSpawnerKey);
        if (spawner == null) {
            return;
        }

        entityFactory.createEntity(EntityIds.ISLAND_CORE_SPAWNER_DISPLAY, voidWorld.island(), e -> {
            e.setTag(EntityTags.ITEM_DISPLAY_KEY, spawner.itemDisplayKey());
            e.setInstance(world.asInstance(), blockPos.asBlockVec().add(0.5, 1.5, 0.5));
            world.setBlock(blockPos, block.withTag(BlockTags.ISLAND_CORE_SPAWNER_KEY, islandCoreSpawnerKey)
                    .withTag(BlockTags.ISLAND_CORE_DISPLAY_ENTITY_ID, e.getUuid()));
        });
    }

    @Override
    public Key blockEntityId() {
        return KEY;
    }

    @Override
    public Collection<Type<?>> types() {
        return List.of(Type.CLICK, Type.TICK, Type.LOAD, Type.VOID_CORE, Type.BLOCK_ENTITY);
    }
}
