package com.fisherl.desolatesky.util;

import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class DistanceUtil {

    private DistanceUtil() {}

    public static Collection<Chunk> getChunksNear(Instance instance, Point position, int distance) {
        final List<Chunk> chunks = new ArrayList<>();
        for (int x = -distance; x <= distance; x++) {
            for (int z = -distance; z <= distance; z++) {
                final int xPos = position.chunkX() + x;
                final int zPos = position.chunkZ() + z;
                if (!instance.isChunkLoaded(xPos, zPos)) {
                    continue;
                }
              final Chunk chunk = instance.getChunk(xPos, zPos);
                if (chunk == null) {
                    continue;
                }
                chunks.add(chunk);
            }
        }
        return chunks;
    }
}
