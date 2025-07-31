package net.desolatesky.listener.type;

import net.desolatesky.block.DSBlockRegistry;
import net.desolatesky.block.handler.DSBlockHandler;
import net.desolatesky.block.handler.InteractionResult;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.instance.InstancePoint;
import net.desolatesky.item.DSItemRegistry;
import net.desolatesky.item.ItemTags;
import net.desolatesky.listener.DSEventHandler;
import net.desolatesky.listener.DSEventHandlers;
import net.desolatesky.listener.DSListener;
import net.desolatesky.listener.EventHandlerResult;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.InventoryUtil;
import net.desolatesky.util.PacketUtil;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.event.Event;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.event.player.PlayerStartDiggingEvent;
import net.minestom.server.event.player.PlayerUseItemOnBlockEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.item.ItemStack;
import net.minestom.server.network.packet.server.play.WorldEventPacket;
import org.jetbrains.annotations.Contract;

import java.util.Collection;

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
                .handler(PlayerBlockBreakEvent.class, this.blockBreakHandler());
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
                final InteractionResult result = blockHandler.onPlayerInteract(
                        player,
                        instance,
                        clickedBlock,
                        clickedPoint,
                        event.getHand(),
                        event.getBlockFace(),
                        cursor
                );
                return switch (result) {
                    case CONSUME_INTERACTION -> EventHandlerResult.CONSUME_EVENT;
                    case PASSTHROUGH -> EventHandlerResult.CONTINUE_LISTENING;
                };
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
                    blockHandler.onPlayerPlace(player, instance, block, event.getBlockPosition(), event.getHand(), blockFace, cursor);
                }
                return EventHandlerResult.CONSUME_EVENT;
            }
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
            final InteractionResult result = blockHandler.onPlayerPunch(
                    player,
                    instance,
                    block,
                    targeted,
                    event.getBlockFace(),
                    Vec.ZERO
            );
            return switch (result) {
                case CONSUME_INTERACTION -> EventHandlerResult.CONSUME_EVENT;
                case PASSTHROUGH -> EventHandlerResult.CONTINUE_LISTENING;
            };
        };
    }

    private DSEventHandler<PlayerBlockBreakEvent> blockBreakHandler() {
        return event -> {
            final Block block = event.getBlock();
            final DSBlockHandler blockHandler = this.blockRegistry.getHandlerForBlock(block);
            if (blockHandler == null || blockHandler.isUnbreakable()) {
                event.setCancelled(true);
                return EventHandlerResult.CONSUME_EVENT;
            }
            if (!(event.getInstance() instanceof final DSInstance instance)) {
                return EventHandlerResult.CONTINUE_LISTENING;
            }
            final DSPlayer player = (DSPlayer) event.getPlayer();
            final Point blockPosition = event.getBlockPosition();
            blockHandler.onPlayerDestroy(player, instance, block, blockPosition);
            final Collection<ItemStack> drops = blockHandler.generateDrops(this.itemRegistry, player.getItemInMainHand(), blockPosition, block);
            final WorldEventPacket packet = PacketUtil.blockBreakPacket(blockPosition, block);
            instance.sendGroupedPacket(packet);
            final InstancePoint<? extends Point> instancePoint = new InstancePoint<>(instance, blockPosition);
            InventoryUtil.addItemsToInventory(player, drops, instancePoint);
            return EventHandlerResult.CONTINUE_LISTENING;
        };
    }


}