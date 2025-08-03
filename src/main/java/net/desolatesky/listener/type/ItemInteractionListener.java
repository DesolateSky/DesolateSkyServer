package net.desolatesky.listener.type;

import net.desolatesky.block.DSBlockRegistry;
import net.desolatesky.block.handler.BlockHandlerResult;
import net.desolatesky.block.handler.DSBlockHandler;
import net.desolatesky.breaking.BreakingManager;
import net.desolatesky.entity.DSEntity;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.item.DSItemRegistry;
import net.desolatesky.item.ItemTags;
import net.desolatesky.item.handler.ItemHandler;
import net.desolatesky.item.handler.ItemInteractionResult;
import net.desolatesky.listener.DSEventHandler;
import net.desolatesky.listener.DSEventHandlers;
import net.desolatesky.listener.DSListener;
import net.desolatesky.listener.EventHandlerResult;
import net.desolatesky.player.DSPlayer;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.event.Event;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.event.player.PlayerCancelDiggingEvent;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.event.player.PlayerFinishDiggingEvent;
import net.minestom.server.event.player.PlayerHandAnimationEvent;
import net.minestom.server.event.player.PlayerStartDiggingEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.event.player.PlayerUseItemOnBlockEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Contract;

public final class ItemInteractionListener implements DSEventHandlers<Event> {

    private final DSBlockRegistry blockRegistry;
    private final DSItemRegistry itemRegistry;

    public ItemInteractionListener(DSBlockRegistry blockRegistry, DSItemRegistry itemRegistry) {
        this.blockRegistry = blockRegistry;
        this.itemRegistry = itemRegistry;
    }

    @Override
    @Contract("_ -> param1")
    public DSListener.Builder<Event> register(DSListener.Builder<Event> builder) {
        return builder.handler(PlayerBlockInteractEvent.class, this.blockInteractHandler())
                .handler(PlayerUseItemOnBlockEvent.class, this.useItemOnBlockHandler())
                .handler(PlayerEntityInteractEvent.class, this.entityInteractHandler())
                .handler(PlayerUseItemEvent.class, this.playerUseItemHandler())
                .handler(EntityAttackEvent.class, this.playerPunchEntityHandler())
                .handler(PlayerHandAnimationEvent.class, this.playerHandAnimationHandler())
                .handler(PlayerStartDiggingEvent.class, this.playerPunchBlockHandler())
                .handler(PlayerCancelDiggingEvent.class, this.playerCancelDiggingHandler())
                .handler(PlayerFinishDiggingEvent.class, this.playerFinishDiggingHandler());
    }

    private DSEventHandler<PlayerBlockInteractEvent> blockInteractHandler() {
        return event -> {
            final DSPlayer player = (DSPlayer) event.getPlayer();
            final Block block = event.getBlock();
            final DSBlockHandler blockHandler = this.blockRegistry.getHandlerForBlock(block);
            if (blockHandler == null) {
                return EventHandlerResult.CONTINUE_LISTENING;
            }
            final Point placementPosition = event.getBlockPosition();
            final BlockHandlerResult.InteractBlock result = blockHandler.onPlayerInteract(
                    player,
                    player.getDSInstance(),
                    block,
                    placementPosition,
                    event.getHand(),
                    event.getBlockFace(),
                    event.getCursorPosition()
            );
            final Block resultBlock = result.resultBlock();
            if (resultBlock != null) {
                player.getDSInstance().setBlock(placementPosition, resultBlock, true);
            }
            if (result.cancelEvent()) {
                event.setCancelled(true);
                event.setBlockingItemUse(true);
            }
            return result.toEventHandlerResult();
        };
    }

    private DSEventHandler<PlayerUseItemOnBlockEvent> useItemOnBlockHandler() {
        return event -> {
            final DSPlayer player = (DSPlayer) event.getPlayer();
            final ItemStack itemStack = player.getItemInMainHand();
            final Key blockId = itemStack.getTag(ItemTags.BLOCK_ID);
            final Point clickedPoint = event.getPosition();
            final DSInstance instance = player.getDSInstance();
            final Block clickedBlock = instance.getBlock(clickedPoint);
            final Point cursor = new Vec(0);
            if (blockId != null) {
                final EventHandlerResult result = this.tryPlaceBlock(player, instance, event.getHand(), itemStack, clickedPoint, clickedBlock, cursor, event.getBlockFace(), blockId);
                if (result.consumes()) {
                    return EventHandlerResult.CONSUME_EVENT;
                }
                return EventHandlerResult.CONTINUE_LISTENING;
            }
            final ItemHandler itemHandler = this.itemRegistry.getItemHandler(itemStack);
            if (itemHandler != null) {
                final ItemInteractionResult result = itemHandler.onInteractBlock(player, player.getDSInstance(), itemStack, event.getHand(), clickedPoint, clickedBlock, cursor, event.getBlockFace());
                final ItemStack newItem = result.newItem();
                if (newItem != null) {
                    player.setItemInHand(event.getHand(), newItem);
                }
                if (result.passthrough()) {
                    return EventHandlerResult.CONTINUE_LISTENING;
                }
            }
            return EventHandlerResult.CONTINUE_LISTENING;
        };
    }

    private DSEventHandler<PlayerEntityInteractEvent> entityInteractHandler() {
        return event -> {
            final DSPlayer player = (DSPlayer) event.getPlayer();
            final ItemStack itemStack = player.getItemInMainHand();
            final ItemHandler itemHandler = this.itemRegistry.getItemHandler(itemStack);
            if (itemHandler == null) {
                return EventHandlerResult.CONTINUE_LISTENING;
            }
            if (!(event.getTarget() instanceof final DSEntity target)) {
                return EventHandlerResult.CONTINUE_LISTENING;
            }
            final ItemInteractionResult result = itemHandler.onInteractEntity(player, player.getDSInstance(), itemStack, event.getHand(), target);
            final ItemStack newItem = result.newItem();
            if (newItem != null) {
                player.setItemInHand(event.getHand(), newItem);
            }
            if (result.passthrough()) {
                return EventHandlerResult.CONTINUE_LISTENING;
            }
            return EventHandlerResult.CONSUME_EVENT;
        };
    }

    private DSEventHandler<PlayerUseItemEvent> playerUseItemHandler() {
        return event -> {
            final DSPlayer player = (DSPlayer) event.getPlayer();
            final ItemStack itemStack = player.getItemInMainHand();
            final ItemHandler itemHandler = this.itemRegistry.getItemHandler(itemStack);
            if (itemHandler == null) {
                return EventHandlerResult.CONTINUE_LISTENING;
            }
            final ItemInteractionResult result = itemHandler.onInteractAir(player, player.getDSInstance(), itemStack, event.getHand());
            final ItemStack newItem = result.newItem();
            if (newItem != null) {
                player.setItemInHand(event.getHand(), newItem);
            }
            if (result.passthrough()) {
                return EventHandlerResult.CONTINUE_LISTENING;
            }
            return EventHandlerResult.CONSUME_EVENT;
        };
    }

    private DSEventHandler<EntityAttackEvent> playerPunchEntityHandler() {
        return event -> {
            if (!(event.getEntity() instanceof final DSPlayer player)) {
                return EventHandlerResult.CONTINUE_LISTENING;
            }
            final ItemStack itemStack = player.getItemInMainHand();
            final ItemHandler itemHandler = this.itemRegistry.getItemHandler(itemStack);
            if (itemHandler == null) {
                return EventHandlerResult.CONTINUE_LISTENING;
            }
            if (!(event.getTarget() instanceof final DSEntity target)) {
                return EventHandlerResult.CONTINUE_LISTENING;
            }
            final ItemInteractionResult result = itemHandler.onPunchEntity(player, player.getDSInstance(), itemStack, target);
            final ItemStack newItem = result.newItem();
            if (newItem != null) {
                player.setItemInHand(PlayerHand.MAIN, newItem);
            }
            if (result.passthrough()) {
                return EventHandlerResult.CONTINUE_LISTENING;
            }
            return EventHandlerResult.CONSUME_EVENT;
        };
    }

    private DSEventHandler<PlayerHandAnimationEvent> playerHandAnimationHandler() {
        return event -> {
            if (!(event.getEntity() instanceof final DSPlayer player)) {
                return EventHandlerResult.CONTINUE_LISTENING;
            }
            final ItemStack itemStack = player.getItemInMainHand();
            final ItemHandler itemHandler = this.itemRegistry.getItemHandler(itemStack);
            if (itemHandler == null) {
                return EventHandlerResult.CONTINUE_LISTENING;
            }
            final Point targeted = player.getTargetBlockPosition((int) player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE).getValue());
            final DSInstance instance = player.getDSInstance();
            if (targeted != null) {
                return EventHandlerResult.CONTINUE_LISTENING;
            }
            final ItemInteractionResult result = itemHandler.onPunchAir(player, instance, itemStack);
            final ItemStack newItem = result.newItem();
            if (newItem != null) {
                player.setItemInHand(event.getHand(), newItem);
            }
            if (result.passthrough()) {
                return EventHandlerResult.CONTINUE_LISTENING;
            }
            return EventHandlerResult.CONSUME_EVENT;
        };
    }

    private DSEventHandler<PlayerStartDiggingEvent> playerPunchBlockHandler() {
        return event -> {
            if (!(event.getEntity() instanceof final DSPlayer player)) {
                return EventHandlerResult.CONTINUE_LISTENING;
            }
            final ItemStack itemStack = player.getItemInMainHand();
            final ItemHandler itemHandler = this.itemRegistry.getItemHandler(itemStack);

            final DSInstance instance = player.getDSInstance();
            if (itemHandler == null) {
                startBreaking(player, instance, event.getBlock(), event.getBlockPosition());
                return EventHandlerResult.CONTINUE_LISTENING;
            }
            final ItemInteractionResult result = itemHandler.onPunchBlock(player, instance, itemStack, event.getBlock(), event.getBlockPosition());
            final ItemStack newItem = result.newItem();
            if (newItem != null) {
                player.setItemInHand(PlayerHand.MAIN, newItem);
            }
            if (result.passthrough()) {
                startBreaking(player, instance, event.getBlock(), event.getBlockPosition());
                return EventHandlerResult.CONTINUE_LISTENING;
            }
            return EventHandlerResult.CONSUME_EVENT;
        };
    }

    private DSEventHandler<PlayerCancelDiggingEvent> playerCancelDiggingHandler() {
        return event -> {
            if (!(event.getPlayer() instanceof final DSPlayer player)) {
                return EventHandlerResult.CONTINUE_LISTENING;
            }
            if (!(event.getInstance() instanceof final DSInstance instance)) {
                return EventHandlerResult.CONTINUE_LISTENING;
            }
            pauseBreaking(player, instance, event.getBlockPosition());
            return EventHandlerResult.CONSUME_EVENT;
        };
    }

    private DSEventHandler<PlayerFinishDiggingEvent> playerFinishDiggingHandler() {
        return event -> {
            if (!(event.getPlayer() instanceof final DSPlayer player)) {
                return EventHandlerResult.CONTINUE_LISTENING;
            }
            if (!(event.getInstance() instanceof final DSInstance instance)) {
                return EventHandlerResult.CONTINUE_LISTENING;
            }
            pauseBreaking(player, instance, event.getBlockPosition());
            return EventHandlerResult.CONSUME_EVENT;
        };
    }

    private static void startBreaking(DSPlayer player, DSInstance instance, Block block, Point blockPosition) {
        if (!instance.canBreakBlock(player, blockPosition, block)) {
            return;
        }
        final BreakingManager breakingManager = instance.breakingManager();
        breakingManager.startBreaking(player, blockPosition.asBlockVec(), block);
    }

    private static void pauseBreaking(DSPlayer player, DSInstance instance, BlockVec pos) {
        final BreakingManager breakingManager = instance.breakingManager();
        breakingManager.pauseBreaking(player, pos);
    }

    private EventHandlerResult tryPlaceBlock(
            DSPlayer player,
            DSInstance instance,
            PlayerHand hand,
            ItemStack itemStack,
            Point clickedPoint,
            Block clickedBlock,
            Point cursor,
            BlockFace blockFace,
            Key blockId
    ) {
        final Block block = this.blockRegistry.create(blockId);
        if (block == null) {
            return EventHandlerResult.CONTINUE_LISTENING;
        }
        final Point placePoint = clickedBlock.registry().isReplaceable() ? clickedPoint : clickedPoint.add(blockFace.toDirection().vec());
        final DSBlockHandler blockHandler = this.blockRegistry.getHandlerForBlock(block);
        if (blockHandler != null) {
            final BlockHandlerResult.Place result = blockHandler.onPlayerPlace(
                    player,
                    instance,
                    block,
                    placePoint,
                    hand,
                    blockFace,
                    cursor
            );
            if (result.cancelEvent()) {
                player.setItemInMainHand(itemStack);
                return result.toEventHandlerResult();
            } else {
                final Block resultBlock = result.resultBlock();
                if (resultBlock != null) {
                    instance.setBlock(placePoint, resultBlock, true);
                }
            }
        } else {
            instance.setBlock(placePoint, block, true);
        }
        player.setItemInMainHand(itemStack.consume(1));
        return EventHandlerResult.CONSUME_EVENT;
    }


}

