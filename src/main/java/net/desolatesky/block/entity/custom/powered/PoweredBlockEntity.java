package net.desolatesky.block.entity.custom.powered;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.entity.BlockEntity;
import net.desolatesky.block.entity.BlockEntityHandler;
import net.desolatesky.block.settings.BlockSettings;
import net.kyori.adventure.key.Key;
import net.minestom.server.utils.Direction;

public abstract class PoweredBlockEntity<E extends PoweredBlockEntity<E>> extends BlockEntity<E> {

    protected int stored;

    public PoweredBlockEntity(Key key, DesolateSkyServer server) {
        super(key, server);
    }

    @Override
    public boolean isTickable() {
        return true;
    }

    /**
     *
     * @return amount of electricity consumed
     */
    protected abstract int consumeElectricity(Direction direction, int amount);

    protected abstract int getTotalElectricity();

    public abstract int getMaxPower();

    protected abstract int getTransferRate();

    protected static abstract class Handler<E extends PoweredBlockEntity<E>> extends BlockEntityHandler<E> {

        public Handler(BlockSettings blockSettings, Class<E> entityClass) {
            super(blockSettings, entityClass);
        }

    }


}
