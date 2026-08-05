package net.desolatesky.block.behavior.impl.crop;

import net.desolatesky.block.behavior.BlockDropBehavior;
import net.desolatesky.block.behavior.GrowthBehavior;
import net.desolatesky.block.behavior.PlaceRequirementsBehavior;
import net.desolatesky.block.behavior.serializer.BlockBehaviorSerializer;
import net.desolatesky.block.property.BlockProperties;
import net.desolatesky.block.property.IntBlockProperty;
import net.desolatesky.item.ItemFactory;
import net.desolatesky.util.BlockUtil;
import net.desolatesky.util.Namespace;
import net.desolatesky.world.DSWorld;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.MaterialKeys;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class CropBehavior extends GrowthBehavior implements BlockDropBehavior, PlaceRequirementsBehavior {

    public static final class Serializer extends BlockBehaviorSerializer<CropBehavior> {

        public Serializer() {
            super(Namespace.key("crop"));
        }

        private static final String MIN_AGE_KEY = "min-age";
        private static final String MAX_AGE_KEY = "max-age";
        private static final String GROWTH_CHANCE_KEY = "growth-chance";
        private static final String DROPPED_ITEM_KEY = "dropped-item";
        private static final String VOID_CROP_KEY = "void-crop";
        private static final String MIN_DROPS_KEY = "min-drops";
        private static final String MAX_DROPS_KEY = "max-drops";
        private static final String REMOVE_WATER_CHANCE_KEY = "remove-water-chance";
        private static final String VOID_CROP_CHANCE_KEY = "void-crop-chance";

        @Override
        public CropBehavior deserialize(java.lang.reflect.Type type, ConfigurationNode node) throws SerializationException {
            final int minAge = node.node(MIN_AGE_KEY).getInt();
            final int maxAge = node.node(MAX_AGE_KEY).getInt();
            final double growthChance = node.node(GROWTH_CHANCE_KEY).getDouble();
            final Key droppedItem = node.node(DROPPED_ITEM_KEY).get(Key.class);
            final Key voidCrop = node.node(VOID_CROP_KEY).get(Key.class);
            final int minDrops = node.node(MIN_DROPS_KEY).getInt();
            final int maxDrops = node.node(MAX_DROPS_KEY).getInt();
            final double removeWaterChance = node.node(REMOVE_WATER_CHANCE_KEY).getDouble();
            final double voidCropChance = node.node(VOID_CROP_CHANCE_KEY).getDouble();
            return new CropBehavior(new IntBlockProperty("age", minAge, maxAge), growthChance, droppedItem, voidCrop, minDrops, maxDrops, removeWaterChance, voidCropChance);
        }

        @Override
        public void serialize(java.lang.reflect.Type type, @org.jspecify.annotations.Nullable CropBehavior obj, ConfigurationNode node) throws SerializationException {
            if (obj == null) {
                return;
            }
            node.node(MIN_AGE_KEY).set(obj.ageProperty.min());
            node.node(MAX_AGE_KEY).set(obj.ageProperty.max());
            node.node(GROWTH_CHANCE_KEY).set(obj.growthChance);
            node.node(DROPPED_ITEM_KEY).set(obj.droppedItem);
            node.node(VOID_CROP_KEY).set(obj.voidCrop);
            node.node(MIN_DROPS_KEY).set(obj.minDrops);
            node.node(MAX_DROPS_KEY).set(obj.maxDrops);
            node.node(REMOVE_WATER_CHANCE_KEY).set(obj.removeWaterChance);
            node.node(VOID_CROP_CHANCE_KEY).set(obj.voidCropChance);
        }

        @Override
        public Class<CropBehavior> behaviorClass() {
            return CropBehavior.class;
        }
    }

    private final Key droppedItem;
    private final Key voidCrop;
    private final int minDrops;
    private final int maxDrops;
    private final double removeWaterChance;
    private final double voidCropChance;

    public CropBehavior(
            IntBlockProperty ageProperty,
            double growthChance,
            Key droppedItem,
            Key voidCrop,
            int minDrops,
            int maxDrops,
            double removeWaterChance,
            double voidCropChance
    ) {
        super(ageProperty, growthChance);
        this.droppedItem = droppedItem;
        this.voidCrop = voidCrop;
        this.minDrops = minDrops;
        this.maxDrops = maxDrops;
        this.removeWaterChance = removeWaterChance;
        this.voidCropChance = voidCropChance;
    }

    @Override
    public Collection<ItemStack> getDrops(DSWorld world, Point pos, Block block, Key blockId, ItemFactory itemFactory, @Nullable ItemStack toolUsed) {
        final Integer age = this.ageProperty.read(block);
        if (age == null) {
            return Collections.emptyList();
        }
        final ItemStack itemStack = itemFactory.getDefaultItem(this.droppedItem);
        if (itemStack == null) {
            return Collections.emptyList();
        }
        final int amount;
        if (!this.ageProperty.isMax(block)) {
            amount = 1;
        } else if (this.minDrops == this.maxDrops) {
            amount = this.minDrops;
        } else {
            amount = world.getRandomGenerator(pos).nextInt(this.minDrops, this.maxDrops + 1);
        }
        final List<ItemStack> results = new ArrayList<>();
        if (this.ageProperty.isMax(block) && world.rollChance(pos, this.voidCropChance)) {
            final ItemStack voidCropItem = itemFactory.getDefaultItem(this.voidCrop);
            if (voidCropItem != null) {
                results.add(voidCropItem);
            }
        }
        results.add(itemStack.withAmount(amount));
        return results;
    }

    @Override
    public boolean canGrow(DSWorld world, Point pos, Block block, Key blockId) {
        final Block under = world.getBlock(pos.sub(0, 1, 0));
        if (!Block.FARMLAND.key().equals(BlockUtil.getBlockId(under))) {
            return false;
        }
        final Integer moisture = BlockProperties.FARMLAND_MOISTURE_PROPERTY.read(under);
        return moisture != null && moisture >= BlockProperties.FARMLAND_MOISTURE_PROPERTY.max();
    }

    @Override
    protected void onMaxGrow(DSWorld world, Point pos, Block block, Key blockId) {
        final Point posUnder = pos.sub(0, 1, 0);
        final Block under = world.getBlock(posUnder);
        if (!Block.FARMLAND.key().equals(BlockUtil.getBlockId(under))) {
            return;
        }
        if (!world.rollChance(pos, this.removeWaterChance)) {
            return;
        }
        world.setBlock(posUnder, BlockProperties.FARMLAND_MOISTURE_PROPERTY.write(under, BlockProperties.FARMLAND_MOISTURE_PROPERTY.min()));
    }

    @Override
    public PlaceRequirementsBehavior.Result checkState(DSWorld world, Point pos, Block block) {
        final Block under = world.getBlock(pos.sub(0, 1, 0));
        final boolean onFarmland = under.key().equals(MaterialKeys.FARMLAND.key());
        return onFarmland ? Result.GOOD : Result.DESTROY_AND_DROP;
    }

    @Override
    public boolean isValidForInitialPlace(DSWorld world, Point pos, Block block) {
        final Block under = world.getBlock(pos.sub(0, 1, 0));
        final boolean onFarmland = under.key().equals(MaterialKeys.FARMLAND.key());
        if (!onFarmland) {
            return false;
        }
        return BlockProperties.FARMLAND_MOISTURE_PROPERTY.isMax(under);
    }

    @Override
    public Collection<Type<?>> types() {
        return List.of(Type.RANDOM_TICK, Type.BLOCK_DROP, Type.PLACE_REQUIREMENTS);
    }
}
