package net.desolatesky.block.handler.custom;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.handler.TransientBlockHandler;
import net.desolatesky.block.settings.BlockSettings;
import net.desolatesky.block.settings.DSBlockSettings;
import net.desolatesky.entity.type.DebrisEntity;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.item.DSItems;
import net.desolatesky.util.Namespace;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public final class DebrisCatcherBlockHandler extends TransientBlockHandler {

    public static final Key KEY = Namespace.key("debris_catcher");

    private static final double COLLECTION_RANGE = 4;
    private long tickNum = 0;

    public DebrisCatcherBlockHandler(DesolateSkyServer server) {
        super(server, DSBlockSettings.DEBRIS_CATCHER);
    }

    @Override
    public void tick(@NotNull Tick tick, DSInstance instance) {
        if (this.tickNum++ % 20 == 0) {
            this.collectDebris(tick.getBlockPosition(), instance);
        }
    }

    private void collectDebris(Point point, DSInstance instance) {
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

}
