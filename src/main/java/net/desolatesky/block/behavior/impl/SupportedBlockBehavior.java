package net.desolatesky.block.behavior.impl;

import net.desolatesky.block.behavior.PlaceRequirementsBehavior;
import net.desolatesky.util.BlockUtil;
import net.desolatesky.world.DSWorld;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;
import net.minestom.server.registry.RegistryKey;
import net.minestom.server.registry.RegistryTag;
import net.minestom.server.utils.Direction;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class SupportedBlockBehavior implements PlaceRequirementsBehavior {

    private final Direction supportDirection;
    private final Collection<RegistryTag<Block>> supportingBlockTags;
    private final Set<Key> supportingBlocks;
    private final Predicate<Block> predicate;
    private final boolean supportsSelf;

    public SupportedBlockBehavior(Direction supportDirection,
                                  Collection<RegistryTag<Block>> supportingBlockTags,
                                  Set<Key> supportingBlocks,
                                  Predicate<Block> predicate,
                                  boolean supportsSelf) {
        this.supportDirection = supportDirection;
        this.supportingBlockTags = supportingBlockTags;
        this.supportingBlocks = supportingBlocks;
        this.predicate = predicate;
        this.supportsSelf = supportsSelf;
    }

    public SupportedBlockBehavior(Direction supportDirection, Collection<RegistryTag<Block>> supportingBlockTags, boolean supportsSelf) {
        this(supportDirection, supportingBlockTags, Collections.emptySet(), _ -> true, supportsSelf);
    }

    public SupportedBlockBehavior(Direction supportDirection, boolean supportsSelf, Set<Key> supportingBlocks) {
        this(supportDirection, Collections.emptySet(), supportingBlocks, _ -> true, supportsSelf);
    }

    @Override
    public Result checkState(DSWorld world, Point pos, Block block) {
        final Point toCheck = pos.add(this.supportDirection.vec());
        final Block at = world.getBlock(toCheck);
        if (!this.predicate.test(at)) {
            return Result.DESTROY_AND_DROP;
        }
        if (this.supportsSelf && BlockUtil.isSameBlock(block, at)) {
            return Result.GOOD;
        }
        if (this.supportingBlocks.contains(at.key())) {
            return Result.GOOD;
        }
        final RegistryKey<Block> blockKey = at.asKey();
        if (blockKey == null) {
            return this.supportingBlocks.isEmpty() ? Result.GOOD : Result.DESTROY_AND_DROP;
        }
        return this.supportingBlockTags.stream().anyMatch(regKey -> regKey.contains(blockKey)) ?
                Result.GOOD :
                Result.DESTROY_AND_DROP;
    }

    @Override
    public boolean isValidForInitialPlace(DSWorld world, Point pos, Block block) {
        return this.checkState(world, pos, block) == Result.GOOD;
    }

    @Override
    public Collection<Type<?>> types() {
        return List.of(Type.PLACE_REQUIREMENTS);
    }
}
