package net.desolatesky.block.behavior.impl;

import net.desolatesky.block.BlockIds;
import net.desolatesky.block.behavior.BlockDropBehavior;
import net.desolatesky.block.behavior.MiningSpeedBehavior;
import net.desolatesky.block.behavior.RandomTickBehavior;
import net.desolatesky.block.behavior.serializer.BlockBehaviorSerializer;
import net.desolatesky.item.ItemFactory;
import net.desolatesky.item.ItemIds;
import net.desolatesky.item.definition.ItemDefinition;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.BlockUtil;
import net.desolatesky.util.Namespace;
import net.desolatesky.world.DSWorld;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.random.RandomGenerator;

public final class DryGrassBehavior implements RandomTickBehavior, BlockDropBehavior {

    public static final class Serializer extends BlockBehaviorSerializer<DryGrassBehavior> {

        public Serializer() {
            super(Namespace.key("dry_grass"));
        }

        private static final String VOID_GRASS_CHANCE_KEY = "void-grass-chance";

        @Override
        public DryGrassBehavior deserialize(java.lang.reflect.Type type, ConfigurationNode node) throws SerializationException {
            final double voidGrassChance = node.node("void-grass-chance").getDouble();
            return new DryGrassBehavior(voidGrassChance);
        }

        @Override
        public void serialize(java.lang.reflect.Type type, @org.jspecify.annotations.Nullable DryGrassBehavior obj, ConfigurationNode node) throws SerializationException {
            if (obj == null) {
                return;
            }
            node.node(VOID_GRASS_CHANCE_KEY).set(obj.voidGrassChance);
        }

        @Override
        public Class<DryGrassBehavior> behaviorClass() {
            return DryGrassBehavior.class;
        }
    }

    public static final DryGrassBehavior DRY_GRASS_BEHAVIOR = new DryGrassBehavior(2);

    private final double voidGrassChance;

    private DryGrassBehavior(double voidGrassChance) {
        this.voidGrassChance = voidGrassChance;
    }

    @Override
    public void onRandomTick(DSWorld world, Point pos, Block block, Key blockId) {
        if (blockId.equals(BlockIds.DRY_GRASS_SEEDS)) {
            world.setBlock(pos, Block.SHORT_DRY_GRASS.key(), Function.identity());
        } else if (block.key().equals(Block.SHORT_DRY_GRASS.key())) {
            if (world.rollChance(pos, this.voidGrassChance)) {
                world.setBlock(pos, BlockIds.VOID_INFUSED_BUSH, Function.identity());
            } else {
                world.setBlock(pos, Block.TALL_DRY_GRASS.key(), Function.identity());
            }
        }
    }

    @Override
    public Collection<ItemStack> getDrops(
            DSWorld world,
            Point pos,
            Block block,
            Key blockId,
            ItemFactory itemFactory,
            @Nullable ItemStack toolUsed
    ) {
        final List<ItemStack> results = new ArrayList<>();
        final ItemDefinition seedDefinition = itemFactory.getItemDefinition(ItemIds.DRY_GRASS_SEED);
        if (seedDefinition == null) {
            return Collections.emptyList();
        }
        final int max = BlockUtil.getBlockId(block).key().equals(BlockIds.DRY_GRASS_SEEDS) ? 1 : 2;
        final RandomGenerator randomGenerator = world.getRandomGenerator(pos);
        results.add(seedDefinition.defaultItemStack().withAmount(randomGenerator.nextInt(1, max + 1)));
        if (blockId.equals(BlockIds.VOID_INFUSED_BUSH)) {
            final ItemStack bush = itemFactory.getDefaultItem(ItemIds.VOID_INFUSED_BUSH);
            if (bush == null) {
                return results;
            }
            results.add(bush);
            return results;
        }
        if (!blockId.equals(Block.TALL_DRY_GRASS.key())) {
            return results;
        }

        final ItemDefinition thatchDefinition = itemFactory.getItemDefinition(ItemIds.THATCH);
        if (thatchDefinition == null) {
            return Collections.emptyList();
        }
        final ItemStack itemStack = thatchDefinition.defaultItemStack()
                .withAmount(randomGenerator.nextInt(1, 5));
        results.add(itemStack);
        return results;
    }

    @Override
    public Collection<Type<?>> types() {
        return List.of(Type.RANDOM_TICK, Type.BLOCK_DROP);
    }
}
