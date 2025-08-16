package net.desolatesky.breaking;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.DSBlockRegistry;
import net.desolatesky.block.handler.DSBlockHandler;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.player.DSPlayer;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.adventure.audience.PacketGroupingAudience;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.network.packet.server.play.BlockBreakAnimationPacket;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public final class BreakingManager {

    // Duration after which the break progress is reset if no hits are registered
    private static final Duration BREAK_TIME_RESET_DURATION = Duration.ofMillis(1000);
    private static final Duration HIT_SOUND_INTERVAL = Duration.ofMillis(200);

    private static final byte MAX_CRACK_PROGRESS = 10;

    private final DesolateSkyServer server;
    private final DSBlockRegistry blockRegistry;
    private final Map<UUID, BreakingData> breakingDataMap;
    private final AtomicInteger blockBreakId = new AtomicInteger(0);

    public BreakingManager(DesolateSkyServer server, Map<UUID, BreakingData> breakingDataMap, DSBlockRegistry blockRegistry) {
        this.server = server;
        this.breakingDataMap = breakingDataMap;
        this.blockRegistry = blockRegistry;
    }

    public void tick() {
        this.breakingDataMap.entrySet().removeIf(entry -> {
            final BreakingData breakingData = entry.getValue();
            final DSPlayer player = breakingData.player();
            final DSInstance instance = breakingData.instance();
            final BlockVec blockPos = breakingData.blockPos();
            if (!player.isOnline()) {
                sendResetBreakProgress(instance, breakingData.id(), blockPos);
                return true;
            }
            final Instance playerInstance = player.getInstance();
            if (!Objects.equals(breakingData.instance(), playerInstance)) {
                sendResetBreakProgress(instance, breakingData.id(), blockPos);
                return true;
            }
            if (!instance.canBreakBlock(player, blockPos, breakingData.block())) {
                sendResetBreakProgress(instance, breakingData.id(), blockPos);
                return false;
            }
            if (breakingData.lastHitTime() == null) {
                sendResetBreakProgress(instance, breakingData.id(), blockPos);
                return true;
            }
            final Duration timeSinceLastHit = Duration.between(breakingData.lastHitTime(), Instant.now());
            if (timeSinceLastHit.compareTo(BREAK_TIME_RESET_DURATION) > 0) {
                sendResetBreakProgress(instance, breakingData.id(), blockPos);
                return true;
            }
            if (!breakingData.currentlyBreaking())  {
                return false;
            }
            final Block block = playerInstance.getBlock(blockPos);
            if (!Objects.equals(block, breakingData.block())) {
                sendResetBreakProgress(instance, breakingData.id(), blockPos);
                return false;
            }
            final DSBlockHandler blockHandler = this.blockRegistry.getHandlerForBlock(block);
            if (blockHandler == null) {
                sendResetBreakProgress(instance, breakingData.id(), blockPos);
                return false;
            }
            final Duration timeSinceLastSound = Duration.between(breakingData.lastSoundTime(), Instant.now());
            if (timeSinceLastSound.compareTo(HIT_SOUND_INTERVAL) > 0) {
                final Sound hitSound = blockHandler.settings().digSound();
                if (hitSound != null) {
                    player.playSound(hitSound, blockPos);
                }
                breakingData.setLastSoundTime(Instant.now());
            }
            final Duration breakTime = blockHandler.calculateBlockBreakTime(this.server, player, block);
            if (breakTime == null || breakTime.isNegative()) {
                sendResetBreakProgress(instance, breakingData.id(), blockPos);
                return true;
            }
            breakingData.hit();
            final Duration currentBreakDuration = breakingData.currentBreakingTime();
            if (currentBreakDuration.compareTo(breakTime) < 0) {
                final byte progress = calculateCrackProgress(currentBreakDuration, breakTime);
                sendBreakProgress(instance, breakingData.id(), blockPos, progress);
                return false;
            }
            // get the face of the block the player is looking at (probably wrong)
            final BlockFace blockFace = BlockFace.fromDirection(player.getPosition().facing()).getOppositeFace();
            breakingData.instance().breakBlock(player, blockPos, block, blockFace);
            sendResetBreakProgress(instance, breakingData.id(), blockPos);
            return true;
        });
    }

    public void startBreaking(DSPlayer player, BlockVec blockPos, Block block) {
        final DSInstance instance = player.getInstance();
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
            sendResetBreakProgress(previousData.instance(), previousData.id(), previousData.blockPos());
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
        sendResetBreakProgress(breakingData.instance(), breakingData.id(), blockPos);
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

    private static void sendResetBreakProgress(PacketGroupingAudience audience, int blockBreakId, BlockVec blockPos) {
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

    private static byte calculateCrackProgress(Duration progress, Duration required) {
        final double percentage = (double) progress.toMillis() / required.toMillis();
        if (percentage >= 1) {
            return MAX_CRACK_PROGRESS;
        }
        return (byte) Math.round(percentage * MAX_CRACK_PROGRESS);
    }

}
