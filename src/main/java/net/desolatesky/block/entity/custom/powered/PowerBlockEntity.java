package net.desolatesky.block.entity.custom.powered;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.settings.BlockSettings;
import net.desolatesky.instance.DSInstance;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.Direction;

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
    protected int consumeElectricity(Direction direction, int amount) {
        final int total = this.getTotalElectricity();
        final int max = this.getMaxElectricity();
        final int allowed = max - total;
        if (allowed <= 0) {
            return 0;
        }
        this.receivedElectricalFlows.merge(direction, amount, Integer::sum);
        return allowed;
    }

    protected int getTotalElectricity() {
        int total = this.stored;
        for (final int value : this.receivedElectricalFlows.values()) {
            total += value;
        }
        return total;
    }

    public abstract int getMaxElectricity();

    protected static class Handler<E extends PowerBlockEntity<E>> extends PoweredBlockEntity.Handler<E> {

        public Handler(BlockSettings blockSettings, Class<E> entityClass) {
            super(blockSettings, entityClass);
        }

        @Override
        public void onTick(DSInstance instance, Block block, Point blockPosition, E entity) {
            entity.receivedElectricalFlows.replaceAll(((direction, amount) -> {
                final Block neighbor = instance.getBlock(blockPosition.add(direction.vec()));
                if (!(neighbor.handler() instanceof final PoweredBlockEntity<?> powered)) {
                    return amount;
                }
                final int received = powered.consumeElectricity(direction, amount);
                return Math.max(0, amount - received);
            }));
        }

    }

}
