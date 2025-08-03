package net.desolatesky.listener.type;

import net.desolatesky.block.DSBlockRegistry;
import net.desolatesky.block.handler.BlockHandlerResult;
import net.desolatesky.block.handler.DSBlockHandler;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.item.DSItemRegistry;
import net.desolatesky.item.ItemTags;
import net.desolatesky.listener.DSEventHandler;
import net.desolatesky.listener.DSEventHandlers;
import net.desolatesky.listener.DSListener;
import net.desolatesky.listener.EventHandlerResult;
import net.desolatesky.player.DSPlayer;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.event.Event;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.event.player.PlayerStartDiggingEvent;
import net.minestom.server.event.player.PlayerUseItemOnBlockEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Contract;

public final class BlockInteractionListener implements DSEventHandlers<Event> {

    private final DSBlockRegistry blockRegistry;
    private final DSItemRegistry itemRegistry;

    public BlockInteractionListener(DSBlockRegistry blockRegistry, DSItemRegistry itemRegistry) {
        this.blockRegistry = blockRegistry;
        this.itemRegistry = itemRegistry;
    }

    @Override
    @Contract("_ -> param1")
    public DSListener.Builder<Event> register(DSListener.Builder<Event> builder) {
        return builder.handler(PlayerUseItemOnBlockEvent.class, this.useItemOnBlockHandler())
                .handler(PlayerBlockPlaceEvent.class, this.blockPlaceHandler())
                .handler(PlayerStartDiggingEvent.class, this.playerPunchBlockHandler())
//                .handler(PlayerBlockBreakEvent.class, this.blockBreakHandler())
                ;
    }

    private DSEventHandler<PlayerUseItemOnBlockEvent> useItemOnBlockHandler() {
        return event -> {
            final DSPlayer player = (DSPlayer) event.getPlayer();
            final Point clickedPoint = event.getPosition();
            final DSInstance instance = player.getDSInstance();
            final Block clickedBlock = instance.getBlock(clickedPoint);
            final Point cursor = new Vec(0);
            final DSBlockHandler blockHandler = this.blockRegistry.getHandlerForBlock(clickedBlock);
            if (blockHandler != null) {
                final BlockHandlerResult.InteractBlock result = blockHandler.onPlayerInteract(
                        player,
                        instance,
                        clickedBlock,
                        clickedPoint,
                        event.getHand(),
                        event.getBlockFace(),
                        cursor
                );
                final Block resultBlock = result.resultBlock();
                if (resultBlock != null) {
                    instance.setBlock(clickedPoint, resultBlock);
                }
                return result.toEventHandlerResult();
            }
            return EventHandlerResult.CONTINUE_LISTENING;
        };
    }


    private DSEventHandler<PlayerBlockPlaceEvent> blockPlaceHandler() {
        return event -> {
            final DSPlayer player = (DSPlayer) event.getPlayer();
            final ItemStack itemStack = player.getItemInMainHand();
            final Key blockId = itemStack.getTag(ItemTags.BLOCK_ID);
            if (blockId != null) {
                final Block block = this.blockRegistry.create(blockId);
                if (block == null) {
                    event.setCancelled(true);
                    return EventHandlerResult.CONTINUE_LISTENING;
                }
                event.setBlock(block);
                final DSBlockHandler blockHandler = this.blockRegistry.getHandlerForBlock(block);
                if (blockHandler != null) {
                    final DSInstance instance = player.getDSInstance();
                    final Point cursor = new Vec(0);
                    final BlockFace blockFace = event.getBlockFace();
                    final BlockHandlerResult.Place result = blockHandler.onPlayerPlace(player, instance, block, event.getBlockPosition(), event.getHand(), blockFace, cursor);
                    final Block resultBlock = result.resultBlock();
                    if (resultBlock != null) {
                        event.setBlock(resultBlock);
                    }
                    if (result.cancelEvent()) {
                        event.setCancelled(true);
                    }
                    return result.toEventHandlerResult();
                }
                return EventHandlerResult.CONSUME_EVENT;
            }
            event.setCancelled(true);
            return EventHandlerResult.CONTINUE_LISTENING;
        };
    }

    private DSEventHandler<PlayerStartDiggingEvent> playerPunchBlockHandler() {
        return event -> {
            if (!(event.getEntity() instanceof final DSPlayer player)) {
                return EventHandlerResult.CONTINUE_LISTENING;
            }
            final Point targeted = event.getBlockPosition();
            final Block block = event.getBlock();
            final DSInstance instance = player.getDSInstance();
            final DSBlockHandler blockHandler = this.blockRegistry.getHandlerForBlock(block);
            if (blockHandler == null) {
                return EventHandlerResult.CONTINUE_LISTENING;
            }
            final BlockHandlerResult result = blockHandler.onPlayerPunch(
                    player,
                    instance,
                    block,
                    targeted,
                    event.getBlockFace(),
                    Vec.ZERO
            );
            return result.toEventHandlerResult();
        };
    }

//    private DSEventHandler<PlayerBlockBreakEvent> blockBreakHandler() {
//        return event -> {
//            final Block block = event.getBlock();
//            final DSBlockHandler blockHandler = this.blockRegistry.getHandlerForBlock(block);
//            if (blockHandler == null || blockHandler.isUnbreakable()) {
//                event.setCancelled(true);
//                return EventHandlerResult.CONSUME_EVENT;
//            }
//            if (!(event.getInstance() instanceof final DSInstance instance)) {
//                return EventHandlerResult.CONTINUE_LISTENING;
//            }
//            final DSPlayer player = (DSPlayer) event.getPlayer();
//            final Point blockPosition = event.getBlockPosition();
//            final ItemStack inHand = player.getItemInMainHand();
//            final ItemHandler itemHandler = this.itemRegistry.getItemHandler(inHand);
//            boolean passthrough = true;
//            if (event.isCancelled()) {
//                return EventHandlerResult.CONSUME_EVENT;
//            }
//            final BlockHandlerResult blockHandlerResult = blockHandler.playerDestroyBlock(this.itemRegistry, player, instance, block, blockPosition);
//            if (blockHandlerResult.cancelEvent()) {
//                event.setCancelled(true);
//                if (blockHandlerResult.consumeEvent()) {
//                    return EventHandlerResult.CONSUME_EVENT;
//                }
//                return EventHandlerResult.CONTINUE_LISTENING;
//            }
//            if (itemHandler != null) {
//                final ItemInteractionResult result = itemHandler.onBreakBlock(player, instance, inHand, block, blockPosition);
//                final ItemStack newItem = result.newItem();
//                if (newItem != null) {
//                    player.setItemInMainHand(newItem);
//                }
//                if (result.cancel()) {
//                    event.setCancelled(true);
//                }
//                passthrough = result.passthrough();
//            }
//
//            return EventHandlerResult.CONTINUE_LISTENING;
//        };
//    }


}