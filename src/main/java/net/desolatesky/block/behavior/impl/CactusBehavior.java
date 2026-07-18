package net.desolatesky.block.behavior.impl;

import net.desolatesky.block.behavior.BlockDropBehavior;
import net.desolatesky.block.behavior.MiningSpeedBehavior;
import net.desolatesky.block.behavior.RandomTickBehavior;
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

public final class CactusBehavior implements RandomTickBehavior, MiningSpeedBehavior, BlockDropBehavior {

    private final double growthChance;

    public CactusBehavior(double growthChance) {
        this.growthChance = growthChance;
    }

    @Override
    public void onRandomTick(DSWorld world, Point pos, Block block, Key blockId) {
        if (!world.rollChance(pos, this.growthChance)) {
            return;
        }
        final Point flowerPos = pos.add(0, 1, 0);
        if (!BlockUtil.isReplaceable(world.getBlock(flowerPos))) {
            return;
        }
        world.setBlock(flowerPos, Block.CACTUS_FLOWER);
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
    public Collection<Type<?>> types() {
        return List.of(Type.RANDOM_TICK, Type.MINING_SPEED, Type.BLOCK_DROP);
    }
}
