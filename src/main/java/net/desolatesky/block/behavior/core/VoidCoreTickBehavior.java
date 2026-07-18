package net.desolatesky.block.behavior.core;

import net.desolatesky.block.behavior.TickBehavior;
import net.desolatesky.block.BlockTags;
import net.desolatesky.entity.EntityFactory;
import net.desolatesky.entity.EntityTags;
import net.desolatesky.item.ItemFactory;
import net.desolatesky.item.ItemTags;
import net.desolatesky.item.definition.ItemDefinition;
import net.desolatesky.loot.LootFactory;
import net.desolatesky.loot.LootTable;
import net.desolatesky.util.Pair;
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
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.Weather;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.random.RandomGenerator;

public final class VoidCoreTickBehavior implements TickBehavior {

    private static final Region BLOCK_REGION = Region.square(new Vec(0.5, 0, 0.5), 0.5, 0, 1);

    private final double generateItemChance;

    public VoidCoreTickBehavior(double generateItemChance) {
        this.generateItemChance = generateItemChance;
    }

    public void removeSpawner(DSWorld world, Point pos, Block block) {
        final UUID entityId = block.getTag(BlockTags.ISLAND_CORE_DISPLAY_ENTITY_ID);
        final Entity entity = world.asInstance().getEntityByUuid(entityId);
        if (entity != null) {
            entity.remove();
        }
        world.setBlock(pos, block.withTag(BlockTags.ISLAND_CORE_SPAWNER_KEY, null)
                .withTag(BlockTags.ISLAND_CORE_DISPLAY_ENTITY_ID, null));
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
        if (randBlock == null || !randBlock.isAir()) {
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
        final EntityFactory entityFactory = world.entityFactory();
        entityFactory.createEntity(entityType, voidWorld.island(), e -> {

        }).ifPresent(islandEntity -> {
            final Point randPos = getRandomEntitySpawnPoint(world, pos, world.getRandomGenerator(pos));
            final Entity entity = islandEntity.asEntity();
            entity.setTag(EntityTags.ISLAND_CORE_MOB, true);
            entity.setInstance(instance, randPos);
        });
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
    public Collection<Type<?>> types() {
        return List.of(Type.TICK, Type.VOID_CORE_BEHAVIOR);
    }
}
