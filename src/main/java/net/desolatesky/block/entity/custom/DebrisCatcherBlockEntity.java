package net.desolatesky.block.entity.custom;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.BlockKeys;
import net.desolatesky.block.entity.TransientBlockEntity;
import net.desolatesky.block.handler.entity.BlockEntityHandler;
import net.desolatesky.block.settings.BlockSettings;
import net.desolatesky.block.settings.DSBlockSettings;
import net.desolatesky.entity.type.DebrisEntity;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.item.DSItems;
import net.desolatesky.item.ItemKeys;
import net.desolatesky.util.Namespace;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public final class DebrisCatcherBlockEntity extends TransientBlockEntity<DebrisCatcherBlockEntity> {

    public static final BlockSettings SETTINGS = BlockSettings.builder(BlockKeys.DEBRIS_CATCHER, DSItems.DEBRIS_CATCHER.create())
            .breakTime(1_000)
            .blockItem(ItemKeys.DEBRIS_CATCHER)
            .build();

    public static final BlockEntityHandler<DebrisCatcherBlockEntity> HANDLER = new Handler();

    private static final double COLLECTION_RANGE = 4;
    private long tickNum = 0;

    public DebrisCatcherBlockEntity(DesolateSkyServer server) {
        super(server, HANDLER);
    }

    private static void collectDebris(Point point, DSInstance instance) {
        final Collection<Entity> collection = instance.getNearbyEntities(point, COLLECTION_RANGE);
        for (final Entity entity : collection) {
            if (!(entity instanceof final DebrisEntity debrisEntity)) {
                continue;
            }
            if (debrisEntity.isRemoved()) {
                continue;
            }
            final Collection<ItemStack> loot = debrisEntity.generateLoot();
            debrisEntity.remove();
            for (final ItemStack itemStack : loot) {
                final ItemEntity itemEntity = new ItemEntity(itemStack);
                itemEntity.setNoGravity(true);
                itemEntity.setInstance(instance, point.add(0.5, 0.0, 0.5));
            }
        }
    }

    @Override
    public boolean isTickable() {
        return true;
    }

    private static class Handler extends BlockEntityHandler<DebrisCatcherBlockEntity> {

        private Handler() {
            super(SETTINGS, DebrisCatcherBlockEntity.class);
        }

        @Override
        public void onTick(DSInstance instance, Block block, Point blockPosition, DebrisCatcherBlockEntity entity) {
            if (entity.tickNum++ % 20 == 0) {
                collectDebris(blockPosition, instance);
            }
        }
    }

}
