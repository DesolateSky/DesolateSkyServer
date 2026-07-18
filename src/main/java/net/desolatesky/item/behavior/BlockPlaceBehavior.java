package net.desolatesky.item.behavior;

import net.desolatesky.block.BlockFactory;
import net.desolatesky.block.definition.BlockDefinition;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.world.DSWorld;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface BlockPlaceBehavior extends ItemBehavior {

    BlockPlaceBehavior BLOCKED = new BlockPlaceBehavior() {
        @Override
        public boolean canPlace(DSWorld world, DSPlayer player, ItemStack itemStack, Point blockPos) {
            return false;
        }

        @Override
        public @Nullable Block getBlockToPlace(BlockFactory blockFactory, DSPlayer player, Point point, ItemStack itemStack) {
            return null;
        }
    };

    boolean canPlace(DSWorld world, DSPlayer player, ItemStack itemStack, Point blockPos);

    static BlockPlaceBehavior blockPlaceBehavior(Key blockId) {
        return new Single(blockId);
    }

    @Nullable Block getBlockToPlace(BlockFactory blockFactory, DSPlayer player, Point point, ItemStack itemStack);

    class Single implements BlockPlaceBehavior {

        private final Key blockId;

        public Single(Key blockId) {
            this.blockId = blockId;
        }

        @Override
        public boolean canPlace(DSWorld world, DSPlayer player, ItemStack itemStack, Point blockPos) {
            return true;
        }

        @Override
        public @Nullable Block getBlockToPlace(BlockFactory blockFactory, DSPlayer player, Point point, ItemStack itemStack) {
            final BlockDefinition blockDefinition = blockFactory.getBlockDefinition(this.blockId);
            if (blockDefinition == null) {
                return null;
            }
            return blockDefinition.defaultBlock();
        }
    }
}
