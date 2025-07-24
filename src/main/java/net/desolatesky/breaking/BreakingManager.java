package net.desolatesky.breaking;

import net.desolatesky.block.BlockTags;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.player.DSPlayer;
import net.minestom.server.adventure.audience.PacketGroupingAudience;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.network.packet.server.play.BlockBreakAnimationPacket;
import net.minestom.server.utils.Direction;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public final class BreakingManager {

    private static final byte MAX_CRACK_PROGRESS = 10;

    private final Map<UUID, BreakingData> breakingDataMap;
    private final AtomicInteger blockBreakId = new AtomicInteger(0);

    public BreakingManager(Map<UUID, BreakingData> breakingDataMap) {
        this.breakingDataMap = breakingDataMap;
    }

    public void tick() {
        this.breakingDataMap.entrySet().removeIf(entry -> {
            final BreakingData breakingData = entry.getValue();
            final DSPlayer player = breakingData.player();
            final DSInstance instance = breakingData.instance();
            final BlockVec blockPos = breakingData.blockPos();
            if (!player.isOnline()) {
                resetBreakProgress(instance, breakingData.id(), blockPos);
                return true;
            }
            final Instance playerInstance = player.getInstance();
            if (!Objects.equals(breakingData.instance(), playerInstance)) {
                resetBreakProgress(instance, breakingData.id(), blockPos);
                return true;
            }
            if (!instance.canBreakBlock(player, blockPos, breakingData.block())) {
                resetBreakProgress(instance, breakingData.id(), blockPos);
                return false;
            }
            if (!breakingData.currentlyBreaking() || breakingData.lastHitTime() == null) {
                return false;
            }
            final Block block = playerInstance.getBlock(blockPos);
            if (!Objects.equals(block, breakingData.block())) {
                resetBreakProgress(instance, breakingData.id(), blockPos);
                return true;
            }
            final Integer breakTime = block.getTag(BlockTags.BREAK_TIME);
            if (breakTime == null || breakTime <= 0) {
                resetBreakProgress(instance, breakingData.id(), blockPos);
                return false;
            }
            breakingData.hit();
            final Duration currentBreakDuration = breakingData.currentBreakingTime();
            if (currentBreakDuration.toMillis() < breakTime) {
                final byte progress = calculateCrackProgress((int) currentBreakDuration.toMillis(), breakTime);
                sendBreakProgress(instance, breakingData.id(), blockPos, progress);
                return false;
            }
            // get the face of the block the player is looking at

            final BlockFace blockFace = BlockFace.fromDirection(player.getPosition().facing()).getOppositeFace();
            breakingData.instance().breakBlock(player, blockPos, block, blockFace);
            return true;
        });
    }

    public void startBreaking(DSPlayer player, BlockVec blockPos, Block block) {
        final DSInstance instance = player.getDSInstance();
        if (instance == null) {
            return;
        }
        final BreakingData previousData = this.breakingDataMap.get(player.getUuid());
        if (previousData != null && previousData.blockPos().equals(blockPos)) {
            previousData.setLastHitTime(Instant.now());
            previousData.setCurrentlyBreaking(true);
            return;
        }
        if (previousData != null) {
            resetBreakProgress(previousData.instance(), previousData.id(), previousData.blockPos());
            this.breakingDataMap.remove(player.getUuid());
        }
        final UUID playerId = player.getUuid();
        final BreakingData breakingData = new BreakingData(instance, player, blockPos, block, this.blockBreakId.getAndIncrement());
        this.breakingDataMap.put(playerId, breakingData);
    }

    public void stopBreaking(DSPlayer player, BlockVec blockPos) {
        final UUID playerId = player.getUuid();
        final BreakingData breakingData = this.breakingDataMap.get(playerId);
        if (breakingData == null || !breakingData.blockPos().equals(blockPos)) {
            return;
        }
        resetBreakProgress(breakingData.instance(), breakingData.id(), blockPos);
        this.breakingDataMap.remove(playerId);
    }

    public void pauseBreaking(DSPlayer player, BlockVec blockPos) {
        final UUID playerId = player.getUuid();
        final BreakingData breakingData = this.breakingDataMap.get(playerId);
        if (breakingData == null || !breakingData.blockPos().equals(blockPos)) {
            return;
        }
        breakingData.setCurrentlyBreaking(false);
    }

    private static void resetBreakProgress(PacketGroupingAudience audience, int blockBreakId, BlockVec blockPos) {
        audience.sendGroupedPacket(
                new BlockBreakAnimationPacket(
                        blockBreakId,
                        blockPos,
                        (byte) -1
                )
        );
    }

    private static void sendBreakProgress(PacketGroupingAudience audience, int blockBreakId, BlockVec blockPos, byte progress) {
        audience.sendGroupedPacket(
                new BlockBreakAnimationPacket(
                        blockBreakId,
                        blockPos,
                        progress
                )
        );
    }

    private static byte calculateCrackProgress(int progress, int required) {
        final double percentage = (double) progress / required;
        if (percentage >= 1) {
            return MAX_CRACK_PROGRESS;
        }
        return (byte) Math.round(percentage * MAX_CRACK_PROGRESS);
    }

}
