package net.desolatesky.block.behavior;

import net.desolatesky.block.MCBlockTags;
import net.desolatesky.block.behavior.impl.SupportedBlockBehavior;
import net.desolatesky.world.DSWorld;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.Direction;

import java.util.Set;

public interface PlaceRequirementsBehavior extends BlockBehavior {

    PlaceRequirementsBehavior DIRT_SUPPORT_REQUIREMENT = new SupportedBlockBehavior(Direction.DOWN, Set.of(MCBlockTags.DIRT, MCBlockTags.GRASS_BLOCKS), false);

    enum Result {
        DESTROY_AND_DROP,
        DESTROY,
        GOOD
    }


    /***
     *
     * @param world the world
     * @param pos the pos
     * @param block the block
     * @return the result of the block's current placement in the world
     */
    Result checkState(DSWorld world, Point pos, Block block);

    /**
     * This method is only checked when a block is newly placed into the world, such as a player
     * placing a block.
     *
     * @param world the world
     * @param pos the block pos
     * @param block the block being placed
     * @return true if the block is allowed to be placed
     */
    boolean isValidForInitialPlace(DSWorld world, Point pos, Block block);

}
