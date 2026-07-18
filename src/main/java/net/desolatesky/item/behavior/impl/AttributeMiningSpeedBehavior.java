package net.desolatesky.item.behavior.impl;

import net.desolatesky.block.BlockFactory;
import net.desolatesky.block.definition.BlockDefinition;
import net.desolatesky.item.behavior.MiningSpeedBehavior;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.world.DSWorld;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;

import java.util.Set;

public final class AttributeMiningSpeedBehavior implements MiningSpeedBehavior {

    private final Set<Key> blockAttributes;
    private final Tag<Double> miningSpeedTag;

    public AttributeMiningSpeedBehavior(Set<Key> blockAttributes, Tag<Double> miningSpeedTag) {
        this.blockAttributes = blockAttributes;
        this.miningSpeedTag = miningSpeedTag;
    }

    @Override
    public int modifyTickSpeed(
            int originalSpeed,
            DSWorld world,
            DSPlayer player,
            ItemStack minedWith,
            Point blockPos,
            Block block
    ) {
        final BlockFactory blockFactory = world.blockFactory();
        final BlockDefinition blockDefinition = blockFactory.getBlockDefinition(block);
        if (blockDefinition == null) {
            return originalSpeed;
        }
        final Double multiplier = minedWith.getTag(this.miningSpeedTag);
        if (multiplier == null) {
            return originalSpeed;
        }
        for (final Key attribute : this.blockAttributes) {
            if (!blockDefinition.hasAttribute(attribute)) {
                continue;
            }
            return (int) Math.ceil(originalSpeed * multiplier);
        }
        return originalSpeed;
    }
}
