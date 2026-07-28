package net.desolatesky.item.listener;

import net.desolatesky.Listener;
import net.desolatesky.block.BlockFactory;
import net.desolatesky.block.behavior.BlockBehavior;
import net.desolatesky.block.behavior.PlaceRequirementsBehavior;
import net.desolatesky.block.definition.BlockDefinition;
import net.desolatesky.item.ItemFactory;
import net.desolatesky.item.behavior.BlockPlaceBehavior;
import net.desolatesky.item.behavior.ItemBehavior;
import net.desolatesky.item.definition.ItemDefinition;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.BlockUtil;
import net.desolatesky.world.DSWorld;
import net.minestom.server.coordinate.Point;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.event.player.PlayerUseItemOnBlockEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.function.Function;

@NotNullByDefault
public final class BlockPlaceListener implements Listener<Event> {

    private final ItemFactory itemFactory;
    private final BlockFactory blockFactory;

    public BlockPlaceListener(ItemFactory itemFactory, BlockFactory blockFactory) {
        this.itemFactory = itemFactory;
        this.blockFactory = blockFactory;
    }

    @Override
    public void register(EventNode<Event> node) {
        this.registerBlockPlace(node);
        this.registerBlockPlaceFromItem(node);
    }

    private void registerBlockPlace(EventNode<Event> node) {
        node.addListener(PlayerBlockPlaceEvent.class, event -> {
            if (!(event.getEntity() instanceof final DSPlayer player)) {
                event.setCancelled(true);
                return;
            }
            if (!(event.getInstance() instanceof final DSWorld world)) {
                event.setCancelled(true);
                return;
            }
            if (!world.canPlaceBlock(player, event.getBlockPosition(), event.getBlock())) {
                event.setCancelled(true);
                return;
            }
            final ItemStack itemStack = player.getItemInHand(event.getHand());
            final ItemDefinition itemDefinition = this.itemFactory.getItemDefinition(itemStack);
            if (itemDefinition == null) {
                event.setCancelled(true);
                return;
            }
            final BlockPlaceBehavior blockPlaceBehavior = itemDefinition.getBehavior(ItemBehavior.Type.BLOCK_PLACE);
            if (blockPlaceBehavior == null) {
                event.setCancelled(true);
                return;
            }
            final Point blockPos = event.getBlockPosition();
            final Block at = world.getBlock(event.getBlockPosition());
            if (!BlockUtil.isReplaceable(at)) {
                event.setCancelled(true);
                return;
            }
            if (!blockPlaceBehavior.canPlace(world, player, itemStack, blockPos)) {
                event.setCancelled(true);
                return;
            }
            final Block block = blockPlaceBehavior.getBlockToPlace(this.blockFactory,
                    player,
                    blockPos,
                    event.getBlockFace(),
                    event.getCursorPosition(),
                    itemStack);
            if (block == null) {
                event.setCancelled(true);
                return;
            }
            final BlockDefinition blockDefinition = this.blockFactory.getBlockDefinition(block);
            if (blockDefinition == null) {
                event.setCancelled(true);
                return;
            }
            final PlaceRequirementsBehavior placeRequirementsBehavior = blockDefinition.getBehavior(BlockBehavior.Type.PLACE_REQUIREMENTS);
            if (placeRequirementsBehavior != null && !placeRequirementsBehavior.isValidForInitialPlace(world, blockPos, block)) {
                event.setCancelled(true);
                return;
            }
            event.setBlock(block);
        });
    }

    private void registerBlockPlaceFromItem(EventNode<Event> node) {
        node.addListener(PlayerUseItemOnBlockEvent.class, event -> {
            if (!(event.getEntity() instanceof final DSPlayer player)) {
                return;
            }
            if (!(event.getInstance() instanceof final DSWorld world)) {
                return;
            }
            final ItemStack itemStack = player.getItemInHand(event.getHand());
            final ItemDefinition itemDefinition = this.itemFactory.getItemDefinition(itemStack);
            if (itemDefinition == null) {
                return;
            }
            final BlockPlaceBehavior blockPlaceBehavior = itemDefinition.getBehavior(ItemBehavior.Type.BLOCK_PLACE);
            if (blockPlaceBehavior == null) {
                return;
            }
            Point blockPos = event.getPosition();
            final Block block = world.getBlock(blockPos);
            if (!BlockUtil.isReplaceable(block)) {
                final Direction direction = player.getPosition().facing();
                blockPos = blockPos.add(direction.vec());
                // check next block
                if (!BlockUtil.isReplaceable(world.getBlock(blockPos))) {
                    return;
                }
            }
            final BlockDefinition blockDefinition = this.blockFactory.getBlockDefinition(block);
            if (blockDefinition == null) {
                return;
            }
            if (!world.canPlaceBlock(player, blockPos, block)) {
                return;
            }
            final PlaceRequirementsBehavior placeRequirementsBehavior = blockDefinition.getBehavior(BlockBehavior.Type.PLACE_REQUIREMENTS);
            if (placeRequirementsBehavior != null && !placeRequirementsBehavior.isValidForInitialPlace(world, blockPos, block)) {
                return;
            }
            if (!blockPlaceBehavior.canPlace(world, player, itemStack, blockPos)) {
                return;
            }
            final Block blockToPlace = blockPlaceBehavior.getBlockToPlace(this.blockFactory,
                    player,
                    blockPos,
                    event.getBlockFace(),
                    event.getCursorPosition(),
                    itemStack);
            if (blockToPlace == null) {
                return;
            }
            world.setBlock(blockPos, BlockUtil.getBlockId(blockToPlace), Function.identity());
        });
    }
}
