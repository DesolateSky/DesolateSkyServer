package net.desolatesky.block.entity;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.handler.entity.BlockEntityHandler;
import net.desolatesky.block.settings.BlockSettings;
import net.desolatesky.instance.DSInstance;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.Nullable;

public class TransientBlockEntity<E extends BlockEntity<E>> extends BlockEntity<E> {

    public TransientBlockEntity(DesolateSkyServer server, BlockEntityHandler<E> handler) {
        super(server, handler);
    }

    @Override
    public @Nullable Block save(DSInstance instance, Point point, Block block) {
        return null;
    }

    @Override
    public void load(Placement placement, DSInstance instance) {

    }

}
