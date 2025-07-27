package net.desolatesky.block.handler;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.settings.BlockSettings;
import net.desolatesky.instance.DSInstance;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;

public class TransientBlockHandler extends DSBlockHandler {

    public TransientBlockHandler(DesolateSkyServer server, BlockSettings blockSettings) {
        super(server, blockSettings);
    }

    public TransientBlockHandler(DesolateSkyServer server, BlockSettings.Builder blockSettings) {
        super(server, blockSettings);
    }

    @Override
    public void save(DSInstance instance, Point point, Block block) {

    }

    @Override
    public void load(CompoundBinaryTag tag, DSInstance instance, Point point, Block block) {

    }

}
