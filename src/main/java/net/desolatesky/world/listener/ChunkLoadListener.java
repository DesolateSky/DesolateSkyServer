package net.desolatesky.world.listener;

import net.desolatesky.Listener;
import net.desolatesky.block.BlockFactory;
import net.desolatesky.block.behavior.BlockBehavior;
import net.desolatesky.block.behavior.listener.LoadBehavior;
import net.desolatesky.block.definition.BlockDefinition;
import net.desolatesky.world.DSWorld;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.instance.InstanceChunkLoadEvent;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.instance.Chunk;
import org.jetbrains.annotations.NotNullByDefault;

@NotNullByDefault
public class ChunkLoadListener implements Listener<InstanceEvent> {

    @Override
    public void register(EventNode<InstanceEvent> node) {
        node.addListener(InstanceChunkLoadEvent.class, event -> {
            if (!(event.getInstance() instanceof final DSWorld world)) {
                return;
            }
            final BlockFactory blockFactory = world.blockFactory();
            final Chunk chunk = event.getChunk();
            chunk.getBlockEntities().forEach((pos, block) -> {
                final BlockDefinition blockDefinition = blockFactory.getBlockDefinition(block);
                if (blockDefinition == null) {
                    return;
                }
                final LoadBehavior loadBehavior = blockDefinition.getBehavior(BlockBehavior.Type.LOAD);
                if (loadBehavior == null) {
                    return;
                }
                loadBehavior.onLoad(world, pos, block);
            });
        });
    }
}
