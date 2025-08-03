package net.desolatesky.util;

import net.desolatesky.block.handler.DSBlockHandler;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.adventure.audience.PacketGroupingAudience;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.server.play.WorldEventPacket;

public final class PacketUtil {

    private PacketUtil() {
        throw new UnsupportedOperationException();
    }

    public static void sendBlockBreak(PacketGroupingAudience audience, DSBlockHandler blockHandler, Point point, Block block, boolean disableRelativeVolume) {
        final Sound breakSound = blockHandler.settings().breakSound();
        if (breakSound != null) {
            audience.playSound(breakSound, point);
        }
        final WorldEventPacket worldEventPacket = new WorldEventPacket(2001, point, block.stateId(), disableRelativeVolume);
        audience.sendGroupedPacket(worldEventPacket);
    }

    public static void sendBlockBreak(PacketGroupingAudience audience, DSBlockHandler blockHandler, Point point, Block block) {
        sendBlockBreak(audience, blockHandler, point, block, false);
    }

}
