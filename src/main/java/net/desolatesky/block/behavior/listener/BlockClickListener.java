package net.desolatesky.block.behavior.listener;

import net.desolatesky.Listener;
import net.desolatesky.block.BlockFactory;
import net.desolatesky.block.behavior.BlockBehavior;
import net.desolatesky.block.behavior.ClickBehavior;
import net.desolatesky.block.definition.BlockDefinition;
import net.desolatesky.item.ItemFactory;
import net.desolatesky.item.behavior.ItemBehavior;
import net.desolatesky.item.definition.ItemDefinition;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.world.DSWorld;
import net.kyori.adventure.key.Key;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.event.player.PlayerStartDiggingEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
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
            final BlockDefinition blockDefinition = this.blockFactory.getBlockDefinition(blockId);
            if (blockDefinition == null) {
                return;
            }
            final ClickBehavior clickBehavior = blockDefinition.getBehavior(BlockBehavior.Type.CLICK);
            if (clickBehavior == null) {
                return;
            }
            final ItemStack heldItem = player.getItemInHand(event.getHand());
            final ClickBehavior.Result result = clickBehavior.onRightClick(world, player, event.getHand(), event.getBlockPosition(), block, heldItem);
            if (result == ClickBehavior.Result.BLOCK_INTERACTION) {
                event.setBlockingItemUse(true);
            }
        });
        node.addListener(PlayerStartDiggingEvent.class, event -> {
            if (!(event.getInstance() instanceof final DSWorld world)) {
                return;
            }
            if (!(event.getPlayer() instanceof final DSPlayer player)) {
                return;
            }
            final Block block = event.getBlock();
            final BlockDefinition blockDefinition = this.blockFactory.getBlockDefinition(block);
            if (blockDefinition == null) {
                return;
            }
            final ClickBehavior clickBehavior = blockDefinition.getBehavior(BlockBehavior.Type.CLICK);
            if (clickBehavior == null) {
                return;
            }
            final ClickBehavior.Result result = clickBehavior.onLeftClick(world, player, event.getBlockPosition(), block, player.getItemInMainHand());
            if (result == ClickBehavior.Result.BLOCK_INTERACTION) {
                event.setCancelled(true);
            }
        });
    }
}
