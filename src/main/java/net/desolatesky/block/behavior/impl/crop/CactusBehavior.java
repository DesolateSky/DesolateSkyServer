package net.desolatesky.block.behavior.impl.crop;

import net.desolatesky.block.behavior.BlockDropBehavior;
import net.desolatesky.block.behavior.RandomTickBehavior;
import net.desolatesky.block.behavior.serializer.BlockBehaviorSerializer;
import net.desolatesky.block.property.IntBlockProperty;
import net.desolatesky.item.ItemFactory;
import net.desolatesky.util.BlockUtil;
import net.desolatesky.util.Namespace;
import net.desolatesky.world.DSWorld;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class CactusBehavior implements RandomTickBehavior, BlockDropBehavior {

    public static final class Serializer extends BlockBehaviorSerializer<CactusBehavior> {

        public Serializer() {
            super(Namespace.key("cactus"));
        }

        private static final String GROWTH_CHANCE_KEY = "growth-chance";
        private static final String FLOWER_CHANCE_KEY = "flower-chance";

        @Override
        public CactusBehavior deserialize(java.lang.reflect.Type type, ConfigurationNode node) throws SerializationException {
            final double growthChance = node.node(GROWTH_CHANCE_KEY).getDouble();
            final double flowerChance = node.node(FLOWER_CHANCE_KEY).getDouble();
            return new CactusBehavior(growthChance, flowerChance);
        }

        @Override
        public void serialize(java.lang.reflect.Type type, @org.jspecify.annotations.Nullable CactusBehavior obj, ConfigurationNode node) throws SerializationException {
            if (obj == null) {
                return;
            }
            node.node(GROWTH_CHANCE_KEY).set(obj.growthChance);
            node.node(FLOWER_CHANCE_KEY).set(obj.flowerChance);
        }

        @Override
        public Class<CactusBehavior> behaviorClass() {
            return CactusBehavior.class;
        }
    }


    private static final IntBlockProperty AGE_PROPERTY = new IntBlockProperty("age", 0, 9);
    private final double growthChance;
    private final double flowerChance;

    public CactusBehavior(double growthChance, double flowerChance) {
        this.growthChance = growthChance;
        this.flowerChance = flowerChance;
    }

    @Override
    public void onRandomTick(DSWorld world, Point pos, Block block, Key blockId) {
        if (AGE_PROPERTY.isMax(block)) {
            this.tryGrow(world, pos, Block.CACTUS_FLOWER, this.flowerChance);
            return;
        }
        final Integer age = AGE_PROPERTY.read(block);
        if (age == null) {
            return;
        }
        final int nextAge = age + 1;
        final Block next = AGE_PROPERTY.write(Block.CACTUS, nextAge);
        final boolean result = this.tryGrow(world, pos, next, this.growthChance);
        if (!result) {
            this.tryGrow(world, pos, Block.CACTUS_FLOWER, this.flowerChance);
        }
    }

    private boolean tryGrow(DSWorld world, Point pos, Block growBlock, double chance) {
        if (!world.rollChance(pos, chance)) {
            return false;
        }

        final Point growPos = pos.add(0, 1, 0);
        if (!BlockUtil.isReplaceable(world.getBlock(growPos))) {
            return false;
        }
        world.setBlock(growPos, growBlock);
        return true;
    }

    @Override
    public Collection<ItemStack> getDrops(DSWorld world, Point pos, Block block, Key blockId, ItemFactory itemFactory, @Nullable ItemStack toolUsed) {
        final ItemStack itemStack = itemFactory.getDefaultItem(Material.CACTUS.key());
        if (itemStack == null) {
            return Collections.emptyList();
        }
        return List.of(itemStack);
    }

    @Override
    public Collection<Type<?>> types() {
        return List.of(Type.RANDOM_TICK, Type.BLOCK_DROP);
    }
}
