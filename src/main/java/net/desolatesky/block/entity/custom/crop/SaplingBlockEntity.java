package net.desolatesky.block.entity.custom.crop;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.BlockProperties;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.structure.tree.TreeStructure;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;

public class SaplingBlockEntity extends CropBlockEntity<SaplingBlockEntity> {

    private final TreeStructure treeStructure;

    public SaplingBlockEntity(Key key, DesolateSkyServer server, TreeStructure treeStructure) {
        super(key, server, BlockProperties.STAGE);
        this.treeStructure = treeStructure;
    }

    @Override
    protected void onMaxGrowth(DSInstance instance, Block block, Point blockPosition, boolean alreadyMax) {
        instance.setBlock(blockPosition, Block.AIR);
        this.treeStructure.place(instance, blockPosition);
    }

}
