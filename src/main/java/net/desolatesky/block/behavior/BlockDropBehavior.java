package net.desolatesky.block.behavior;

import net.desolatesky.item.ItemFactory;
import net.desolatesky.world.DSWorld;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface BlockDropBehavior extends BlockBehavior{

    Collection<ItemStack> getDrops(DSWorld world,
                                   Point pos,
                                   Block block,
                                   Key blockId,
                                   ItemFactory itemFactory,
                                   @Nullable ItemStack toolUsed);

    static BlockDropBehavior constantDrop(Key itemKey) {
        return new BlockDropBehavior() {
            @Override
            public Collection<ItemStack> getDrops(DSWorld world, Point pos, Block block, Key blockId, ItemFactory itemFactory, @Nullable ItemStack toolUsed) {
                final ItemStack itemStack = itemFactory.getDefaultItem(itemKey);
                if (itemStack == null) {
                    return Collections.emptyList();
                }
                return List.of(itemStack);
            }

            @Override
            public Collection<Type<?>> types() {
                return List.of(Type.BLOCK_DROP);
            }
        };
    }

}
