package net.desolatesky.item.tool;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.entity.DSEntity;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.item.ItemTags;
import net.desolatesky.item.category.ItemCategory;
import net.desolatesky.item.handler.ItemHandler;
import net.desolatesky.item.handler.ItemInteractionResult;
import net.desolatesky.item.handler.breaking.MiningLevel;
import net.desolatesky.item.handler.breaking.MiningLevels;
import net.desolatesky.item.handler.breaking.calculator.BreakTimeCalculator;
import net.desolatesky.item.tool.action.BreakBlockActionData;
import net.desolatesky.item.tool.action.ClickAirActionData;
import net.desolatesky.item.tool.action.ClickBlockActionData;
import net.desolatesky.item.tool.action.ClickEntityActionData;
import net.desolatesky.item.tool.part.ToolPart;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.PhysicalClickType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.item.ItemStack;

import java.util.Collection;

public final class ToolItemHandler extends ItemHandler {

    public ToolItemHandler(Key key, BreakTimeCalculator breakTimeCalculator, Collection<ItemCategory> categories, CompoundBinaryTag tagData) {
        super(key, breakTimeCalculator, categories, tagData);
    }

    @Override
    public MiningLevel getMiningLevelFor(Block block) {
        return MiningLevels.NONE;
    }

    public ToolItemHandler(Key key, BreakTimeCalculator breakTimeCalculator, Collection<ItemCategory> categories) {
        super(key, breakTimeCalculator, categories);
    }

    @Override
    public ItemInteractionResult onBreakBlock(DSPlayer player, DSInstance instance, ItemStack usedItem, Block block, Point blockPoint) {
        final Tool tool = usedItem.getTag(ItemTags.TOOL);
        if (tool == null) {
            return ItemInteractionResult.noEffect();
        }
        final DesolateSkyServer server = player.desolateSkyServer();
        final ToolPart headPart = tool.getHeadPart(server.itemRegistry(), server.toolPartRegistry());
        final BreakBlockActionData actionData = new BreakBlockActionData(
                player, instance, blockPoint, block, usedItem
        );
        final ItemStack headPartItem = tool.getHeadPartItem(server.itemRegistry());
        return headPart.modifyBreak(actionData, headPartItem);
    }

    @Override
    public ItemInteractionResult onInteractBlock(DSPlayer player, DSInstance instance, ItemStack usedItem, PlayerHand hand, Point blockPoint, Block block, Point cursorPosition, BlockFace blockFace) {
        final Tool tool = usedItem.getTag(ItemTags.TOOL);
        if (tool == null) {
            return ItemInteractionResult.noEffect();
        }
        final DesolateSkyServer server = player.desolateSkyServer();
        final ToolPart headPart = tool.getHeadPart(server.itemRegistry(), server.toolPartRegistry());
        final ClickBlockActionData actionData = new ClickBlockActionData(
                player, instance, usedItem, block, blockPoint, hand, PhysicalClickType.RIGHT_CLICK
        );
        final ItemStack headPartItem = tool.getHeadPartItem(server.itemRegistry());
        return headPart.modifyClick(actionData, headPartItem);
    }

    @Override
    public ItemInteractionResult onInteractEntity(DSPlayer player, DSInstance instance, ItemStack usedItem, PlayerHand hand, DSEntity interacted) {
        final Tool tool = usedItem.getTag(ItemTags.TOOL);
        if (tool == null) {
            return ItemInteractionResult.noEffect();
        }
        final DesolateSkyServer server = player.desolateSkyServer();
        final ToolPart headPart = tool.getHeadPart(server.itemRegistry(), server.toolPartRegistry());
        final ClickEntityActionData actionData = new ClickEntityActionData(
                player, instance, usedItem, hand, interacted, PhysicalClickType.RIGHT_CLICK
        );
        final ItemStack headPartItem = tool.getHeadPartItem(server.itemRegistry());
        return headPart.modifyClick(actionData, headPartItem);
    }

    @Override
    public ItemInteractionResult onInteractAir(DSPlayer player, DSInstance instance, ItemStack usedItem, PlayerHand hand) {
        final Tool tool = usedItem.getTag(ItemTags.TOOL);
        if (tool == null) {
            return ItemInteractionResult.noEffect();
        }
        final DesolateSkyServer server = player.desolateSkyServer();
        final ToolPart headPart = tool.getHeadPart(server.itemRegistry(), server.toolPartRegistry());
        final ClickAirActionData actionData = new ClickAirActionData(
                player, instance, usedItem, hand, PhysicalClickType.RIGHT_CLICK
        );
        final ItemStack headPartItem = tool.getHeadPartItem(server.itemRegistry());
        return headPart.modifyClick(actionData, headPartItem);
    }

    @Override
    public ItemInteractionResult onPunchBlock(DSPlayer player, DSInstance instance, ItemStack usedItem, Block block, Point blockPoint) {
        final Tool tool = usedItem.getTag(ItemTags.TOOL);
        if (tool == null) {
            return ItemInteractionResult.noEffect();
        }
        final DesolateSkyServer server = player.desolateSkyServer();
        final ToolPart headPart = tool.getHeadPart(server.itemRegistry(), server.toolPartRegistry());
        final ClickBlockActionData actionData = new ClickBlockActionData(
                player, instance, usedItem, block, blockPoint, PlayerHand.MAIN, PhysicalClickType.LEFT_CLICK
        );
        final ItemStack headPartItem = tool.getHeadPartItem(server.itemRegistry());
        return headPart.modifyClick(actionData, headPartItem);
    }

    @Override
    public ItemInteractionResult onPunchAir(DSPlayer player, DSInstance instance, ItemStack usedItem) {
        final Tool tool = usedItem.getTag(ItemTags.TOOL);
        if (tool == null) {
            return ItemInteractionResult.noEffect();
        }
        final DesolateSkyServer server = player.desolateSkyServer();
        final ToolPart headPart = tool.getHeadPart(server.itemRegistry(), server.toolPartRegistry());
        final ClickAirActionData actionData = new ClickAirActionData(
                player, instance, usedItem, PlayerHand.MAIN, PhysicalClickType.LEFT_CLICK
        );
        final ItemStack headPartItem = tool.getHeadPartItem(server.itemRegistry());
        return headPart.modifyClick(actionData, headPartItem);
    }

    @Override
    public ItemInteractionResult onPunchEntity(DSPlayer player, DSInstance instance, ItemStack usedItem, DSEntity interacted) {
        final Tool tool = usedItem.getTag(ItemTags.TOOL);
        if (tool == null) {
            return ItemInteractionResult.noEffect();
        }
        final DesolateSkyServer server = player.desolateSkyServer();
        final ToolPart headPart = tool.getHeadPart(server.itemRegistry(), server.toolPartRegistry());
        final ClickEntityActionData actionData = new ClickEntityActionData(
                player, instance, usedItem, PlayerHand.MAIN, interacted, PhysicalClickType.LEFT_CLICK
        );
        final ItemStack headPartItem = tool.getHeadPartItem(server.itemRegistry());
        return headPart.modifyClick(actionData, headPartItem);
    }

}
