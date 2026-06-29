package com.fisherl.desolatesky.breaking;

import com.fisherl.desolatesky.block.BlockFactory;
import com.fisherl.desolatesky.block.behavior.BlockBehavior;
import com.fisherl.desolatesky.block.behavior.MiningSpeedBehavior;
import com.fisherl.desolatesky.player.DSPlayer;
import com.fisherl.desolatesky.util.BlockUtil;
import com.fisherl.desolatesky.world.DSWorld;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.server.play.BlockBreakAnimationPacket;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public final class BreakingManager {

    // Duration after which the break progress is reset if no hits are registered
    private static final Duration BREAK_TIME_RESET_DURATION = Duration.ofMillis(1000);
    private static final Duration HIT_SOUND_INTERVAL = Duration.ofMillis(200);
    private static final byte MAX_CRACK_PROGRESS = 10;

    private final AtomicInteger blockBreakId = new AtomicInteger(0);
    private final Map<UUID, MiningData> playerMiningData = new HashMap<>();
    private final DSWorld world;
    private final BlockFactory blockFactory;

    public BreakingManager(DSWorld world, BlockFactory blockFactory) {
        this.world = world;
        this.blockFactory = blockFactory;
    }

    public void tick() {
        this.playerMiningData.entrySet().removeIf(entry -> {
            final MiningData miningData = entry.getValue();
            final Point blockPos = miningData.blockPos();
            final Block block = this.world.getBlock(blockPos);
            final DSPlayer player = miningData.player();
            final int time = this.getTicksToMine(blockPos, block, player);
            if (time < -1) {
                sendResetBreakProgress(player, miningData.blockBreakId(), blockPos);
                return true;
            }
            final byte progress = calculateCrackProgress((int)Duration.between(miningData.startTime(), Instant.now()).toMillis(), time * 50);
            if (progress >= MAX_CRACK_PROGRESS) {
                this.breakBlock(player, miningData.blockBreakId(), blockPos);
                return true;
            }
            sendBreakProgress(player, miningData.blockBreakId(), blockPos, progress);
            return false;
        });
    }

    private void breakBlock(DSPlayer player, int blockBreakId, Point blockPos) {
        this.world.setBlock(blockPos, Block.AIR);
        sendResetBreakProgress(player, blockBreakId, blockPos);
    }

    public void startBreaking(DSPlayer player, Point blockPos, Block block) {
        final int time = this.getTicksToMine(blockPos, block, player);
        if (time < 0) {
            return;
        }
        this.addMiningData(player, blockPos);
    }

    public void stopBreaking(DSPlayer player, Point blockPos) {
        final MiningData miningData = this.playerMiningData.get(player.getUuid());
        if (miningData == null) {
            return;
        }
        this.playerMiningData.remove(player.getUuid());
        sendResetBreakProgress(player, miningData.blockBreakId, blockPos);
    }

    private int getTicksToMine(Point blockPos, Block block, DSPlayer player) {
        return this.blockFactory.getBlockDefinition(block)
                .flatMap(definition -> definition.getBehavior(BlockBehavior.Type.MINING_SPEED))
                .map(behavior -> behavior.getTicksToMine(this.world, blockPos, block, player))
                .orElse(100);
    }

    private void addMiningData(DSPlayer player, Point blockPos) {
        final UUID uuid = player.getUuid();
        final int nextId = this.blockBreakId.getAndIncrement();
        final MiningData old = this.playerMiningData.put(uuid, new MiningData(blockPos, Instant.now(), player, nextId));
        if (old != null) {
            sendResetBreakProgress(player, nextId, blockPos);
        }
    }

    private static void sendResetBreakProgress(DSPlayer player, int blockBreakId, Point blockPos) {
        player.sendPacket(
                new BlockBreakAnimationPacket(
                        blockBreakId,
                        blockPos,
                        (byte) -1
                )
        );
    }

    private static void sendBreakProgress(DSPlayer player, int blockBreakId, Point blockPos, byte progress) {
        player.sendPacket(
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
            return MAX_CRACK_PROGRESS + 1;
        }
        return (byte) Math.round(Math.min(MAX_CRACK_PROGRESS - 1, percentage * MAX_CRACK_PROGRESS));
    }

    private record MiningData(Point blockPos, Instant startTime, DSPlayer player, int blockBreakId) {

    }

}
