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

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class PowerBlockEntity<E extends PowerBlockEntity<E>> extends PoweredBlockEntity<E> {

    public PowerBlockEntity(Key key, DesolateSkyServer server) {
        super(key, server);
    }

    /**
     * @return amount of electricity consumed
     */
    @Override
    protected int consumeElectricity(Direction direction, int amount) {
        final int total = this.getTotalElectricity();
        final int max = this.getMaxPower();
        final int allowed = max - total;
        if (allowed <= 0) {
            return 0;
        }
        final int transfer = Math.min(allowed, amount);
        this.addStored(transfer);
        return transfer;
    }

    @Override
    protected int getTotalElectricity() {
        return this.getStored();
    }

    protected abstract List<Direction> getOutputDirections();

    protected abstract int getFlow(Direction direction);

    public boolean canTransferPowerTo(Point sourcePoint, Block sourceBlock, Point destination, PowerBlockEntity<?> destinationBlockEntity) {
        if (sourcePoint.distanceSquared(destination) != 1) {
            return false;
        }
        final Direction direction = DirectionUtil.getDirectionBetween(sourcePoint, destination);
        if (direction == null) {
            return false;
        }
        final int available = Math.max(0, destinationBlockEntity.getMaxPower() - destinationBlockEntity.getTotalElectricity());
        return available != 0 && this.getFlow(direction) > 0;
    }

    @Override
    public @NotNull Block save(DSInstance instance, Point point, Block block) {
        return block;
    }

    @Override
    public void load(Placement placement, DSInstance instance) {

    }

    protected static class Handler<E extends PowerBlockEntity<E>> extends PoweredBlockEntity.Handler<E> {

        public Handler(BlockSettings blockSettings, Class<E> entityClass) {
            super(blockSettings, entityClass);
        }

        @Override
        protected void onTick(DSInstance instance, Block block, Point blockPosition, E entity) {
            final List<Direction> outputDirections = entity.getOutputDirections();
            final AtomicInteger transferred = new AtomicInteger(0);
            outputDirections.forEach(direction -> {
                final Block neighbor = instance.getBlock(blockPosition.add(direction.vec()));
                if (!(neighbor.handler() instanceof final PoweredBlockEntity<?> powered)) {
                    return;
                }
                final int transferAmount = entity.getStored();
                final int received = powered.consumeElectricity(direction, Math.min(entity.getTransferRate(), transferAmount));
                transferred.addAndGet(received);
            });
            entity.subtractStored(Math.max(0, transferred.get()));
        }

    }

}
