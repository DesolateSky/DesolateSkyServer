package net.desolatesky.block.behavior.impl;

import net.desolatesky.block.behavior.BlockDropBehavior;
import net.desolatesky.block.behavior.MiningSpeedBehavior;
import net.desolatesky.item.ItemFactory;
import net.desolatesky.item.definition.ItemDefinition;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.world.DSWorld;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class WoodPlanksBehavior implements MiningSpeedBehavior, BlockDropBehavior {

    private final Key itemKey;

    public WoodPlanksBehavior(Key itemKey) {
        this.itemKey = itemKey;
    }

    @Override
    public int getTicksToMine(DSWorld world, Point blockPos, Block block, DSPlayer player) {
        return 5 * 20;
    }

    @Override
    public Collection<ItemStack> getDrops(DSWorld world, Point pos, Block block, Key blockId, ItemFactory itemFactory, @Nullable ItemStack toolUsed) {
        final ItemDefinition itemDefinition = itemFactory.getItemDefinition(this.itemKey);
        if (itemDefinition == null) {
            return Collections.emptyList();
        }
        return List.of(itemDefinition.defaultItemStack());
    }

    @Override
    public Collection<Type<?>> types() {
        return List.of(Type.MINING_SPEED, Type.BLOCK_DROP);
    }
}
