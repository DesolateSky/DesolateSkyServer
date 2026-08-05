package net.desolatesky.world.listener;

import net.desolatesky.Listener;
import net.desolatesky.block.BlockFactory;
import net.desolatesky.block.behavior.BlockBehavior;
import net.desolatesky.block.behavior.listener.LoadBehavior;
import net.desolatesky.block.definition.BlockDefinition;
import net.desolatesky.island.Island;
import net.desolatesky.island.IslandUnloadEvent;
import net.desolatesky.world.DSWorld;
import net.desolatesky.world.WorldManager;
import net.desolatesky.world.WorldType;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.CoordConversion;
import net.minestom.server.coordinate.Point;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.instance.InstanceChunkLoadEvent;
import net.minestom.server.instance.Chunk;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.UUID;

@NotNullByDefault
public class ChunkLoadListener implements Listener<Event> {

    private final WorldManager worldManager;

    public ChunkLoadListener(WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    @Override
    public void register(EventNode<Event> node) {
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
                final Point worldPos = CoordConversion.chunkBlockRelativeGetGlobal(pos.blockX(), pos.blockY(), pos.blockZ(), chunk.getChunkX(), chunk.getChunkZ());
                loadBehavior.onLoad(world, worldPos, block);
            });
        });
        node.addListener(IslandUnloadEvent.class, event -> {
            final Island island = event.island();
            for (final UUID member : island.getMembers()) {
                if (MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(member) != null) {
                    return;
                }
            }
            for (final WorldType worldType : WorldType.values()) {
                if (worldType.hubWorld()) {
                    continue;
                }
                final UUID worldId = island.getWorldId(worldType);
                this.worldManager.unloadWorld(worldId);
            }
        });
    }
}
