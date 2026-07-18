package net.desolatesky.block.setting;

import net.desolatesky.util.BlockUtil;
import net.desolatesky.world.DSWorld;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.registry.RegistryKey;
import net.minestom.server.registry.RegistryTag;
import net.minestom.server.utils.Direction;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

public interface SupportedBlockSetting extends BlockSetting {

    static SupportedBlockSetting tagged(Direction supportDirection, boolean supportsSelf, Collection<Key> supportingBlockTags) {
        return new Impl(supportDirection, supportingBlockTags, Collections.emptySet(), supportsSelf, _ -> true);
    }

    static SupportedBlockSetting blocks(Direction supportDirection, boolean supportsSelf, Set<Key> supportingBlocks) {
        return new Impl(supportDirection, Collections.emptySet(), supportingBlocks, supportsSelf, _ -> true);
    }

    static SupportedBlockSetting tagged(Direction supportDirection,
                                        boolean supportsSelf,
                                        Collection<Key> supportingBlockTags,
                                        Predicate<Block> predicate
    ) {
        return new Impl(supportDirection, supportingBlockTags, Collections.emptySet(), supportsSelf, predicate);
    }

    static SupportedBlockSetting blocks(Direction supportDirection,
                                        boolean supportsSelf,
                                        Set<Key> supportingBlocks,
                                        Predicate<Block> predicate) {
        return new Impl(supportDirection, Collections.emptySet(), supportingBlocks, supportsSelf, predicate);
    }

    class Impl implements SupportedBlockSetting {

        private final Direction supportDirection;
        private final Collection<RegistryTag<Block>> supportingBlockTags;
        private final Set<Key> supportingBlocks;
        private final Predicate<Block> predicate;
        private final boolean supportsSelf;

        private Impl(
                Direction supportDirection,
                Collection<Key> supportingBlockTags,
                Set<Key> supportingBlocks,
                boolean supportsSelf,
                Predicate<Block> predicate
        ) {
            this.supportDirection = supportDirection;
            this.supportingBlockTags = supportingBlockTags.stream().map(Block.staticRegistry()::getTag)
                    .filter(Objects::nonNull)
                    .toList();
            this.supportingBlocks = supportingBlocks;
            this.supportsSelf = supportsSelf;
            this.predicate = predicate;
        }

        @Override
        public Result checkState(DSWorld world, Point pos, Block block) {
            final Point toCheck = pos.add(this.supportDirection.vec());
            final Block at = world.getBlock(toCheck);
            if (!this.predicate.test(at)) {
                return Result.DESTROY_AND_DROP;
            }
            if (this.supportsSelf && BlockUtil.getBlockId(block).equals(BlockUtil.getBlockId(at))) {
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
    }
}
