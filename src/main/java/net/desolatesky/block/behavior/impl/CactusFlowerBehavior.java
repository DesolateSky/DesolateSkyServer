package net.desolatesky.block.behavior.impl;

import net.desolatesky.block.BlockTags;
import net.desolatesky.block.behavior.BlockDropBehavior;
import net.desolatesky.block.behavior.ClickBehavior;
import net.desolatesky.block.behavior.MiningSpeedBehavior;
import net.desolatesky.block.behavior.RandomTickBehavior;
import net.desolatesky.block.behavior.serializer.BlockBehaviorSerializer;
import net.desolatesky.fluid.FluidMeasurement;
import net.desolatesky.item.ItemFactory;
import net.desolatesky.item.ItemIds;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.InventoryUtil;
import net.desolatesky.util.Namespace;
import net.desolatesky.world.DSWorld;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.random.RandomGenerator;

public final class CactusFlowerBehavior implements RandomTickBehavior, BlockDropBehavior, ClickBehavior {

    public static final class Serializer extends BlockBehaviorSerializer<CactusFlowerBehavior> {

        public Serializer() {
            super(Namespace.key("cactus_flower"));
        }

        private static final String WATER_CHANCE_KEY = "water-chance";
        private static final String WATER_BUCKETS_INCREMENT_KEY = "water-buckets-increment";
        private static final String MAX_WATER_BUCKETS_KEY = "max-water-buckets";

        @Override
        public CactusFlowerBehavior deserialize(java.lang.reflect.Type type, ConfigurationNode node) throws SerializationException {
            final double waterChance = node.node(WATER_CHANCE_KEY).getDouble();
            final double waterBucketsIncrement = node.node(WATER_BUCKETS_INCREMENT_KEY).getDouble();
            final int maxWaterBuckets = node.node(MAX_WATER_BUCKETS_KEY).getInt();
            return new CactusFlowerBehavior(waterChance, waterBucketsIncrement, maxWaterBuckets);
        }

        @Override
        public void serialize(java.lang.reflect.Type type, @org.jspecify.annotations.Nullable CactusFlowerBehavior obj, ConfigurationNode node) throws SerializationException {
            if (obj == null) {
                return;
            }
            node.node(WATER_CHANCE_KEY).set(obj.waterChance);
            node.node(WATER_BUCKETS_INCREMENT_KEY).set(obj.waterBucketsIncrement);
            node.node(MAX_WATER_BUCKETS_KEY).set(obj.maxWaterBuckets);
        }

        @Override
        public Class<CactusFlowerBehavior> behaviorClass() {
            return CactusFlowerBehavior.class;
        }
    }

    private final double waterChance;
    private final double waterBucketsIncrement;
    private final int maxWaterBuckets;

    public CactusFlowerBehavior(double waterChance, double waterBucketsIncrement, int maxWaterBuckets) {
        this.waterChance = waterChance;
        this.waterBucketsIncrement = waterBucketsIncrement;
        this.maxWaterBuckets = maxWaterBuckets;
    }

    @Override
    public void onRandomTick(DSWorld world, Point pos, Block block, Key blockId) {
        Double waterLevel = block.getTag(BlockTags.CACTUS_FLOWER_WATER);
        if (waterLevel == null) {
            waterLevel = 0.0;
        }
        if (world.rollChance(pos, this.waterChance)) {
            waterLevel += this.waterBucketsIncrement;
            waterLevel = Math.max(waterLevel, this.maxWaterBuckets);
            world.setBlock(pos, block.withTag(BlockTags.CACTUS_FLOWER_WATER, waterLevel));
        }
        if (waterLevel >= FluidMeasurement.BOTTLE.buckets()) {
            this.spawnWater(world, pos);
        }
    }

    private void spawnWater(DSWorld world, Point pos) {
        final RandomGenerator randomGenerator = world.getRandomGenerator(pos);
        final Point blockPos = pos.asBlockVec().add(0.5, 0, 0.5);
        for (int i = 0; i < 5; i++) {
            final double xOffset = randomGenerator.nextDouble(-0.5, 0.5);
            final double zOffset = randomGenerator.nextDouble(-0.5, 0.5);
            final ParticlePacket packet = new ParticlePacket(Particle.DRIPPING_WATER, blockPos.add(xOffset, 1, zOffset), new Vec(0), 0, 1);
            world.sendGroupedPacket(packet);
        }
    }

    @Override
    public Collection<ItemStack> getDrops(DSWorld world, Point pos, Block block, Key blockId, ItemFactory itemFactory, @Nullable ItemStack toolUsed) {
//        final ItemStack itemStack = itemFactory.getDefaultItem(Material.CACTUS_FLOWER.key());
//        if (itemStack == null) {
        return Collections.emptyList();
//        }
//        return List.of(itemStack);
    }

    @Override
    public Result onRightClick(DSWorld world, DSPlayer player, PlayerHand hand, Point clickedPos, Block clickedBlock, ItemStack clickedWith) {
        if (!clickedWith.material().equals(Material.GLASS_BOTTLE)) {
            return Result.ALLOW;
        }
        Double waterLevel = clickedBlock.getTag(BlockTags.CACTUS_FLOWER_WATER);
        if (waterLevel == null || waterLevel < FluidMeasurement.BOTTLE.buckets()) {
            return Result.BLOCK_INTERACTION;
        }
        waterLevel -= FluidMeasurement.BOTTLE.buckets();
        world.setBlock(clickedPos, clickedBlock.withTag(BlockTags.CACTUS_FLOWER_WATER, waterLevel));
        InventoryUtil.subtractFromHeldItem(player, hand, 1);
        final ItemStack waterBottle = world.itemFactory().getDefaultItem(ItemIds.WATER_BOTTLE);
        if (waterBottle == null) {
            return Result.BLOCK_INTERACTION;
        }
        InventoryUtil.addItemToInventory(player, waterBottle);
        return Result.BLOCK_INTERACTION;
    }

    @Override
    public Result onLeftClick(DSWorld world, DSPlayer player, Point clickedPos, Block clickedBlock, ItemStack clickedWith) {
        return Result.ALLOW;
    }

    @Override
    public Collection<Type<?>> types() {
        return List.of(Type.RANDOM_TICK, Type.BLOCK_DROP, Type.CLICK);
    }
}
