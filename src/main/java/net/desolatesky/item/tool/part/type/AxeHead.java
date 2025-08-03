package net.desolatesky.item.tool.part.type;

import net.desolatesky.block.BlockTags;
import net.desolatesky.block.DSBlock;
import net.desolatesky.block.DSBlockRegistry;
import net.desolatesky.block.handler.DSBlockHandler;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.item.handler.ItemInteractionResult;
import net.desolatesky.item.tool.action.ClickBlockActionData;
import net.desolatesky.item.tool.action.ToolActionData;
import net.desolatesky.item.tool.part.ToolPart;
import net.desolatesky.item.tool.part.ToolPartType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AxeHead implements ToolPart {

    private final Key key;

    public AxeHead(Key key) {
        this.key = key;
    }

    @Override
    public ItemInteractionResult modifyClick(ClickBlockActionData action, ItemStack toolPart) {
        final Block clicked = action.clickedBlock();
        final DSInstance instance = action.instance();
        final DSBlockRegistry blockRegistry = action.getBlockRegistry();
        final DSBlockHandler blockHandler = blockRegistry.getHandlerForBlock(clicked);
        if (!action.clickType().isRightClick() || action.player().isSneaking()) {
            return ItemInteractionResult.noEffect();
        }
        if (blockHandler == null) {
            return ItemInteractionResult.noEffect();
        }
        final Key stripsToKey = blockHandler.settings().getSetting(BlockTags.STRIPS_TO);
        if (stripsToKey == null) {
            return ItemInteractionResult.noEffect();
        }
        final DSBlock block = blockRegistry.getBlock(stripsToKey);
        if (block == null) {
            return ItemInteractionResult.noEffect();
        }
        instance.setBlock(action.blockPosition(), block.create(action.getServer().blockEntities()));
        return new ItemInteractionResult(null, false, false);
    }

    @Override
    public List<Component> getDescription(ItemStack toolPartItem) {
        return List.of();
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
