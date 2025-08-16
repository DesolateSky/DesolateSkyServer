package net.desolatesky.block.entity.custom.powered;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.settings.BlockSettings;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.util.DirectionUtil;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;

public abstract class PowerBlockEntity<E extends PowerBlockEntity<E>> extends PoweredBlockEntity<E> {

    public PowerBlockEntity(Key key, DesolateSkyServer server) {
        super(key, server);
    }

    public abstract int getTransferRate();

    public abstract int getFlowInDirection(Direction direction);

    public abstract boolean canFlowInDirection(Direction direction);

    public boolean canTransferPowerTo(Point point, Block block, Point destination, PowerBlockEntity<?> destinationBlockEntity) {
        if (point.distanceSquared(destination) != 1) {
            return false;
        }
        final Direction direction = DirectionUtil.getDirectionBetween(point, destination);
        if (direction == null) {
            return false;
        }
        final int available = Math.max(0, destinationBlockEntity.getMaxPower() - destinationBlockEntity.getStored());
        return available != 0 && this.getFlowInDirection(direction) > 0;
    }

    @Override
    public @NotNull Block save(DSInstance instance, Point point, Block block) {
        return super.save(instance, point, block);
    }

    @Override
    public void load(Placement placement, DSInstance instance) {
        super.load(placement, instance);
    }

    protected static class Handler<E extends PowerBlockEntity<E>> extends PoweredBlockEntity.Handler<E> {

        public Handler(BlockSettings blockSettings, Class<E> entityClass) {
            super(blockSettings, entityClass);
        }

    }

}
