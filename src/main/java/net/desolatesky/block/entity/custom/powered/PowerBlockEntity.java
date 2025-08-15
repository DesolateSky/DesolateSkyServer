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

import java.util.HashMap;
import java.util.Map;

public abstract class PowerBlockEntity<E extends PowerBlockEntity<E>> extends PoweredBlockEntity<E> {

    protected final Map<Direction, Integer> receivedElectricalFlows;

    public PowerBlockEntity(Key key, DesolateSkyServer server) {
        super(key, server);
        this.receivedElectricalFlows = new HashMap<>();
    }

    /**
     *
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
        this.receivedElectricalFlows.merge(direction, transfer, Integer::sum);
        return transfer;
    }

    @Override
    protected int getTotalElectricity() {
        int total = this.stored;
        for (int value : this.receivedElectricalFlows.values()) {
            total += value;
        }
        return total;
    }

    protected int getFlow(Direction direction) {
        return this.receivedElectricalFlows.getOrDefault(direction, 0);
    }

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
            entity.receivedElectricalFlows.replaceAll(((direction, amount) -> {
                final Block neighbor = instance.getBlock(blockPosition.add(direction.vec()));
                if (!(neighbor.handler() instanceof final PoweredBlockEntity<?> powered)) {
                    return amount;
                }
                final int received = powered.consumeElectricity(direction, Math.min(entity.getTransferRate(), amount));
                return Math.max(0, amount - received);
            }));
        }

    }

}
