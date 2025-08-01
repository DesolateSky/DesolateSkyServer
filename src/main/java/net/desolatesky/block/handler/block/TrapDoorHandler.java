package net.desolatesky.block.handler.block;

import net.desolatesky.block.BlockProperties;
import net.desolatesky.block.handler.BlockHandlerResult;
import net.desolatesky.block.handler.DSBlockHandler;
import net.desolatesky.block.settings.BlockSettings;
import net.desolatesky.instance.DSInstance;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;

public class TrapDoorHandler extends DSBlockHandler {

    public TrapDoorHandler(BlockSettings blockSettings) {
        super(blockSettings);
    }

    @Override
    public BlockHandlerResult onPlayerInteract(Player player, DSInstance instance, Block block, Point blockPosition, PlayerHand hand, BlockFace face, Point cursorPosition) {
        if (player.isSneaking() && player.getItemInMainHand().material().isBlock()) {
            return BlockHandlerResult.PASS_THROUGH;
        }
        final Boolean open = BlockProperties.OPEN.get(block);
        if (open == null) {
            return BlockHandlerResult.PASS_THROUGH;
        }
        final Block newBlock;
        if (open) {
            newBlock = BlockProperties.OPEN.set(block, false);
        } else {
            newBlock = BlockProperties.OPEN.set(block, true);
        }
        instance.setBlock(blockPosition, newBlock);
        player.sendMessage("New trap door state: " + newBlock.getProperty(BlockProperties.OPEN.name()));
        return BlockHandlerResult.CONSUME;
    }

}
