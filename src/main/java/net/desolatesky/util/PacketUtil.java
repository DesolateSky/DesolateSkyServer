package net.desolatesky.util;

import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.server.play.WorldEventPacket;

public final class PacketUtil {

    private PacketUtil() {
        throw new UnsupportedOperationException();
    }

    public static WorldEventPacket blockBreakPacket(Point point, Block block, boolean disableRelativeVolume) {
        return new WorldEventPacket(2001, point, block.stateId(), disableRelativeVolume);
    }

    public static WorldEventPacket blockBreakPacket(Point point, Block block) {
        return blockBreakPacket(point, block, false);
    }

}
