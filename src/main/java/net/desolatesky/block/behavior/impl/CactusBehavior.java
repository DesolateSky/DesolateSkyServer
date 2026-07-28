package net.desolatesky.block.behavior.impl;

import net.desolatesky.block.MCBlockTags;
import net.desolatesky.block.MCMaterialTags;
import net.desolatesky.block.behavior.BlockDropBehavior;
import net.desolatesky.block.behavior.MiningSpeedBehavior;
import net.desolatesky.block.behavior.PlaceRequirementsBehavior;
import net.desolatesky.block.behavior.RandomTickBehavior;
import net.desolatesky.block.property.IntBlockProperty;
import net.desolatesky.item.ItemFactory;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.BlockUtil;
import net.desolatesky.world.DSWorld;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class CactusBehavior implements RandomTickBehavior, MiningSpeedBehavior, BlockDropBehavior, PlaceRequirementsBehavior {

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
    public int getTicksToMine(DSWorld world, Point blockPos, Block block, DSPlayer player) {
        return 60;
    }

    @Override
    public Result checkState(DSWorld world, Point pos, Block block) {
        final Block under = world.getBlock(pos.sub(0, 1, 0));
        final boolean good = MCBlockTags.isDirtOrGrass(under) || BlockUtil.isSameBlock(under, block);
        return good ? Result.GOOD : Result.DESTROY_AND_DROP;
    }

    @Override
    public boolean isValidForInitialPlace(DSWorld world, Point pos, Block block) {
        return this.checkState(world, pos, block) == Result.GOOD;
    }

    @Override
    public Collection<Type<?>> types() {
        return List.of(Type.RANDOM_TICK, Type.MINING_SPEED, Type.BLOCK_DROP, Type.PLACE_REQUIREMENTS);
    }
}
