package com.fisherl.desolatesky.block.behavior.listener;

import com.fisherl.desolatesky.Listener;
import com.fisherl.desolatesky.block.BlockFactory;
import com.fisherl.desolatesky.block.behavior.BlockBehavior;
import com.fisherl.desolatesky.block.behavior.ClickBehavior;
import com.fisherl.desolatesky.player.DSPlayer;
import com.fisherl.desolatesky.world.DSWorld;
import net.kyori.adventure.key.Key;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.event.player.PlayerStartDiggingEvent;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNullByDefault;

@NotNullByDefault
public final class BlockClickListener implements Listener<Event> {

    private final BlockFactory blockFactory;

    public BlockClickListener(BlockFactory blockFactory) {
        this.blockFactory = blockFactory;
    }

    @Override
    public void register(EventNode<Event> node) {
        node.addListener(PlayerBlockInteractEvent.class, event -> {
            if (!(event.getInstance() instanceof final DSWorld world)) {
                return;
            }
            if (!(event.getPlayer() instanceof final DSPlayer player)) {
                return;
            }
            final Block block = event.getBlock();
            final Key blockId = this.blockFactory.getBlockId(block);
            this.blockFactory.getBlockDefinition(blockId)
                    .flatMap(def -> def.getBehavior(BlockBehavior.Type.CLICK))
                    .ifPresent(behavior -> {
                        final ClickBehavior.Result result = behavior.onRightClick(world, player, event.getHand(), event.getBlockPosition(), block, player.getItemInHand(event.getHand()));
                        if (result == ClickBehavior.Result.BLOCK_INTERACTION) {
                            event.setBlockingItemUse(true);
                        }
                    });
        });
        node.addListener(PlayerStartDiggingEvent.class, event -> {
            if (!(event.getInstance() instanceof final DSWorld world)) {
                return;
            }
            if (!(event.getPlayer() instanceof final DSPlayer player)) {
                return;
            }
            final Block block = event.getBlock();
            final Key blockId = this.blockFactory.getBlockId(block);
            this.blockFactory.getBlockDefinition(blockId)
                    .flatMap(def -> def.getBehavior(BlockBehavior.Type.CLICK))
                    .ifPresent(behavior -> {
                        final ClickBehavior.Result result = behavior.onLeftClick(world, player, event.getBlockPosition(), block, player.getItemInMainHand());
                        if (result == ClickBehavior.Result.BLOCK_INTERACTION) {
                            event.setCancelled(true);
                        }
                    });
        });
    }
}
