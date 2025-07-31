package net.desolatesky.item.tool.part.type;

import net.desolatesky.block.BlockTags;
import net.desolatesky.block.DSBlock;
import net.desolatesky.block.DSBlockRegistry;
import net.desolatesky.block.handler.DSBlockHandler;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.item.tool.action.ClickBlockActionData;
import net.desolatesky.item.tool.action.ToolActionData;
import net.desolatesky.item.tool.part.ToolPart;
import net.desolatesky.item.tool.part.ToolPartType;
import net.kyori.adventure.key.Key;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AxeHead implements ToolPart {

    private final Key key;

    public AxeHead(Key key) {
        this.key = key;
    }

    @Override
    public ToolActionData.Result modifyClick(ClickBlockActionData action, ItemStack toolPart) {
        final Block clicked = action.clickedBlock();
        final DSInstance instance = action.instance();
        final DSBlockRegistry blockRegistry = action.getBlockRegistry();
        final DSBlockHandler blockHandler = blockRegistry.getHandlerForBlock(clicked);
        if (blockHandler == null) {
            return ToolActionData.Result.NOT_CONSUME_NOT_CANCEL;
        }
        final Key stripsToKey = blockHandler.settings().getSetting(BlockTags.STRIPS_TO);
        if (stripsToKey == null) {
            return ToolActionData.Result.NOT_CONSUME_NOT_CANCEL;
        }
        final DSBlock block = blockRegistry.getBlock(stripsToKey);
        if (block == null) {
            return ToolActionData.Result.NOT_CONSUME_NOT_CANCEL;
        }
        instance.setBlock(action.blockPosition(), block.create(action.getServer().blockEntities()));
        return ToolActionData.Result.CONSUME_CANCEL;
    }

    @Override
    public @NotNull Key key() {
        return this.key;
    }

    @Override
    public ToolPartType type() {
        return ToolPartType.AXE_HEAD;
    }

}
