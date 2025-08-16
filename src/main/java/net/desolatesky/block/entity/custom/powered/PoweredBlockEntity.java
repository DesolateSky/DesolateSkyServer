package net.desolatesky.block.entity.custom.powered;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.BlockTags;
import net.desolatesky.block.entity.BlockEntity;
import net.desolatesky.block.entity.BlockEntityHandler;
import net.desolatesky.block.settings.BlockSettings;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.util.DirectionUtil;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;

public abstract class PoweredBlockEntity<E extends PoweredBlockEntity<E>> extends BlockEntity<E> {

    private int stored;

    public PoweredBlockEntity(Key key, DesolateSkyServer server) {
        super(key, server);
        this.stored = 0;
    }

    protected void setStored(int amount) {
        this.stored = Math.max(0, Math.min(amount, this.getMaxPower()));
    }

    protected void subtractStored(int amount) {
        this.setStored(this.stored - amount);
    }

    protected void addStored(int amount) {
        this.setStored(this.stored + amount);
    }

    protected int getStored() {
        return this.stored;
    }

    @Override
    public boolean isTickable() {
        return true;
    }

    @Override
    public @NotNull Block save(DSInstance instance, Point point, Block block) {
        return block.withTag(BlockTags.STORED_POWER, this.stored);
    }

    @Override
    public void load(Placement placement, DSInstance instance) {
        this.stored = Objects.requireNonNullElse(placement.getBlock().getTag(BlockTags.MAX_POWER), 0);
    }

    /**
     * @return amount of electricity consumed
     */
    protected int consumeElectricity(Direction direction, int amount) {
        final int total = this.getStored();
        final int max = this.getMaxPower();
        final int allowed = max - total;
        if (allowed <= 0) {
            return 0;
        }
        final int transfer = Math.min(allowed, amount);
        this.addStored(transfer);
        return transfer;
    }

    public abstract int getMaxPower();

    public abstract int getTickInterval();

    /**
     * @return directions from which this block can receive power
     */
    public @Unmodifiable List<Direction> getInputDirections() {
        return DirectionUtil.ALL_DIRECTIONS;
    }

    public boolean canReceivePowerFrom(Direction direction) {
        return this.getInputDirections().contains(direction);
    }

    public boolean isFull() {
        return this.getStored() >= this.getMaxPower();
    }

    protected static abstract class Handler<E extends PoweredBlockEntity<E>> extends BlockEntityHandler<E> {

        public Handler(BlockSettings blockSettings, Class<E> entityClass) {
            super(blockSettings, entityClass);
        }

        // Receives power from neighboring blocks
        @Override
        protected void onTick(long tick, DSInstance instance, Block block, Point blockPosition, E entity) {
            if (tick % entity.getTickInterval() != 0) {
                return;
            }
            final List<Direction> inputDirections = entity.getInputDirections();
            for (Direction inputDirection : inputDirections) {
                final Direction outputDirection = inputDirection.opposite();
                if (entity.isFull()) {
                    return;
                }
                final Block neighbor = instance.getBlock(blockPosition.add(outputDirection.vec()));
                if (!(neighbor.handler() instanceof final PowerBlockEntity<?> powered)) {
                    continue;
                }
                if (!entity.canReceivePowerFrom(inputDirection)) {
                    continue;
                }
                final int transferAmount = powered.getStored();
                final int received = entity.consumeElectricity(inputDirection, Math.min(powered.getTransferRate(), transferAmount));
                powered.subtractStored(received);
            }

        }

    }


}
