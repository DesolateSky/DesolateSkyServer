package net.desolatesky.instance.listener;

import net.desolatesky.block.DSBlockRegistry;
import net.desolatesky.block.handler.DSBlockHandler;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.instance.InstancePoint;
import net.desolatesky.instance.lobby.LobbyInstance;
import net.desolatesky.item.DSItemRegistry;
import net.desolatesky.listener.DSListener;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.InventoryUtil;
import net.desolatesky.util.PacketUtil;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.network.packet.server.play.WorldEventPacket;

import java.util.Collection;

public final class InstanceListener implements DSListener {

    private final DSBlockRegistry blockRegistry;
    private final DSItemRegistry itemRegistry;

    public InstanceListener(DSBlockRegistry blockRegistry, DSItemRegistry itemRegistry) {
        this.blockRegistry = blockRegistry;
        this.itemRegistry = itemRegistry;
    }

    public static final EventNode<Event> LOBBY_NODE = EventNode.all("lobby-instance-listener")
            .addChild(EventNode.type("block-break", EventFilter.INSTANCE, (_, instance) -> instance instanceof LobbyInstance)
                    .addListener(PlayerBlockBreakEvent.class, event -> event.setCancelled(true))
                    .addListener(PlayerBlockPlaceEvent.class, event -> event.setCancelled(true))
            );

    @Override
    public void register(EventNode<Event> root) {
        root.addChild(this.blockBreakNode())
                .addChild(LOBBY_NODE);
    }

    private EventNode<? extends Event> blockBreakNode() {
        return EventNode.type("player-join", EventFilter.PLAYER)
                .addListener(PlayerBlockBreakEvent.class, event -> {
                            final Block block = event.getBlock();
                            final DSBlockHandler blockHandler = this.blockRegistry.getHandlerForBlock(block);
                            if (blockHandler == null || blockHandler.isUnbreakable()) {
                                event.setCancelled(true);
                                return;
                            }
                            if (!(event.getInstance() instanceof final DSInstance instance)) {
                                return;
                            }
                            final DSPlayer player = (DSPlayer) event.getPlayer();
                            final Point blockPosition = event.getBlockPosition();
                            blockHandler.onPlayerDestroy(player, instance, block, blockPosition);
                            final Collection<ItemStack> drops = blockHandler.generateDrops(this.itemRegistry, player.getItemInMainHand(), blockPosition, block);
                            final WorldEventPacket packet = PacketUtil.blockBreakPacket(blockPosition, block);
                            instance.sendGroupedPacket(packet);
                            final InstancePoint<? extends Point> instancePoint = new InstancePoint<>(instance, blockPosition);
                            InventoryUtil.addItemsToInventory(player, drops, instancePoint);
                        }
                );
    }

}
