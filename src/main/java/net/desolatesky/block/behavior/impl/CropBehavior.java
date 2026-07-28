package net.desolatesky.block.behavior.impl;

import net.desolatesky.block.behavior.BlockDropBehavior;
import net.desolatesky.block.behavior.GrowthBehavior;
import net.desolatesky.block.behavior.MiningSpeedBehavior;
import net.desolatesky.block.behavior.PlaceRequirementsBehavior;
import net.desolatesky.block.property.BlockProperties;
import net.desolatesky.block.property.IntBlockProperty;
import net.desolatesky.item.ItemFactory;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.BlockUtil;
import net.desolatesky.world.DSWorld;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.MaterialKeys;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class CropBehavior extends GrowthBehavior implements BlockDropBehavior, MiningSpeedBehavior, PlaceRequirementsBehavior {

    private final Key droppedItem;
    private final Key voidCrop;
    private final int ticksToMine;
    private final int minDrops;
    private final int maxDrops;
    private final double removeWaterChance;
    private final double voidCropChance;

    public CropBehavior(
            IntBlockProperty ageProperty,
            double growthChance,
            Key droppedItem,
            Key voidCrop,
            int ticksToMine,
            int minDrops,
            int maxDrops,
            double removeWaterChance,
            double voidCropChance
    ) {
        super(ageProperty, growthChance);
        this.droppedItem = droppedItem;
        this.voidCrop = voidCrop;
        this.ticksToMine = ticksToMine;
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
    public int getTicksToMine(DSWorld world, Point blockPos, Block block, DSPlayer player) {
        return this.ticksToMine;
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
        return List.of(Type.RANDOM_TICK, Type.MINING_SPEED, Type.BLOCK_DROP, Type.PLACE_REQUIREMENTS);
    }
}
