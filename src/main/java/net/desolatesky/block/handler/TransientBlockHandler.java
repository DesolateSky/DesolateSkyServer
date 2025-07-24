package net.desolatesky.block.handler;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.loot.table.LootTable;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;

public class TransientBlockHandler extends DSBlockHandler {

    public TransientBlockHandler(DesolateSkyServer server, Key key, LootTable loot, boolean stateless) {
        super(server, key, loot, stateless);
    }

    public TransientBlockHandler(DesolateSkyServer server, Key key, boolean stateless) {
        super(server, key, stateless);
    }

    @Override
    public void save(DSInstance instance, Point point, Block block) {

    }

    @Override
    public void load(CompoundBinaryTag tag, DSInstance instance, Point point, Block block) {

    }

}
