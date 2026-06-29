package com.fisherl.desolatesky.block.setting;

import com.fisherl.desolatesky.util.BlockUtil;
import com.fisherl.desolatesky.world.DSWorld;
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

public interface SupportedBlockSetting extends BlockSetting {

    static SupportedBlockSetting tagged(Direction supportDirection, Collection<Key> supportingBlockTags) {
        return new Impl(supportDirection, supportingBlockTags, Collections.emptySet());
    }

    static SupportedBlockSetting blocks(Direction supportDirection, Set<Key> supportingBlocks) {
        return new Impl(supportDirection, Collections.emptySet(), supportingBlocks);
    }

    class Impl implements SupportedBlockSetting {

        private final Direction supportDirection;
        private final Collection<RegistryTag<Block>> supportingBlockTags;
        private final Set<Key> supportingBlocks;

        private Impl(Direction supportDirection, Collection<Key> supportingBlockTags, Set<Key> supportingBlocks) {
            this.supportDirection = supportDirection;
            this.supportingBlockTags = supportingBlockTags.stream().map(Block.staticRegistry()::getTag)
                    .filter(Objects::nonNull)
                    .toList();
            this.supportingBlocks = supportingBlocks;
        }

        @Override
        public Result checkState(DSWorld world, Point pos, Block block) {
            final Point toCheck = pos.add(this.supportDirection.vec());
            final Block at = world.getBlock(toCheck);
            if (BlockUtil.getBlockId(block).equals(BlockUtil.getBlockId(at))) {
                return Result.GOOD;
            }
            if (this. supportingBlocks.contains(at.key())) {
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
