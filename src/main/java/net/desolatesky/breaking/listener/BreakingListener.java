package net.desolatesky.breaking.listener;

import net.desolatesky.Listener;
import net.desolatesky.breaking.BreakingManager;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.world.DSWorld;
import net.minestom.server.coordinate.Point;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerCancelDiggingEvent;
import net.minestom.server.event.player.PlayerFinishDiggingEvent;
import net.minestom.server.event.player.PlayerStartDiggingEvent;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNullByDefault;

@NotNullByDefault
public final class BreakingListener implements Listener<InstanceEvent> {

    public BreakingListener() {
    }

    @Override
    public void register(EventNode<InstanceEvent> node) {
        this.registerStartDigging(node);
        this.registerCancelDigging(node);
        this.registerFinishDigging(node);
    }



    private void registerStartDigging(EventNode<InstanceEvent> node) {
        node.addListener(PlayerStartDiggingEvent.class, event -> {
            if (!(event.getEntity() instanceof final DSPlayer player)) {
                return;
            }
            final ItemStack itemStack = player.getItemInMainHand();

            if (!(player.getInstance() instanceof final DSWorld world)) {
                return;
            }
            startBreaking(player, world, event.getBlock(), event.getBlockPosition());
        });
    }

    private void registerCancelDigging(EventNode<InstanceEvent> node) {
        node.addListener(PlayerCancelDiggingEvent.class, event -> {
            if (!(event.getPlayer() instanceof final DSPlayer player)) {
                return;
            }
            if (!(event.getInstance() instanceof final DSWorld world)) {
                return;
            }
            pauseBreaking(player, world, event.getBlockPosition());
        });
    }

    private void registerFinishDigging(EventNode<InstanceEvent> node) {
        node.addListener(PlayerFinishDiggingEvent.class, event -> {
            if (!(event.getPlayer() instanceof final DSPlayer player)) {
                return;
            }
            if (!(event.getInstance() instanceof final DSWorld world)) {
                return;
            }
            pauseBreaking(player, world, event.getBlockPosition());
        });
    }

    private static void startBreaking(DSPlayer player, DSWorld world, Block block, Point blockPosition) {
        if (!world.canBreakBlock(player, blockPosition, block)) {
            return;
        }
        final BreakingManager breakingManager = world.breakingManager();
        breakingManager.startBreaking(world, player, blockPosition.asBlockVec(), block);
    }

    private static void pauseBreaking(DSPlayer player, DSWorld world, Point pos) {
        final BreakingManager breakingManager = world.breakingManager();
        breakingManager.stopBreaking(player, pos);
    }
}
