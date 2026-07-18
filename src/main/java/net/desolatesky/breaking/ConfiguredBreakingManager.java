package net.desolatesky.breaking;

import net.desolatesky.block.BlockFactory;
import net.desolatesky.block.behavior.BlockBehavior;
import net.desolatesky.block.behavior.MiningSpeedBehavior;
import net.desolatesky.block.definition.BlockDefinition;
import net.desolatesky.item.behavior.ItemBehavior;
import net.desolatesky.item.definition.ItemDefinition;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.world.DSWorld;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.server.play.BlockBreakAnimationPacket;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public final class ConfiguredBreakingManager implements BreakingManager {

    // Duration after which the break progress is reset if no hits are registered
    private static final Duration BREAK_TIME_RESET_DURATION = Duration.ofMillis(1000);
    private static final Duration HIT_SOUND_INTERVAL = Duration.ofMillis(200);
    private static final byte MAX_CRACK_PROGRESS = 10;

    private final AtomicInteger blockBreakId = new AtomicInteger(0);
    private final Map<UUID, MiningData> playerMiningData = new HashMap<>();
    private final BlockFactory blockFactory;

    public ConfiguredBreakingManager(BlockFactory blockFactory) {
        this.blockFactory = blockFactory;
    }

    @Override
    public void tick(DSWorld world) {
        this.playerMiningData.entrySet().removeIf(entry -> {
            final MiningData miningData = entry.getValue();
            final Point blockPos = miningData.blockPos();
            final Block block = world.getBlock(blockPos);
            final DSPlayer player = miningData.player();
            final int time = this.getTicksToMine(world, blockPos, block, player);
            if (time < -1) {
                sendResetBreakProgress(player, miningData.blockBreakId(), blockPos);
                return true;
            }
            final byte progress = calculateCrackProgress((int) Duration.between(miningData.startTime(), Instant.now()).toMillis(), time * 50);
            if (progress >= MAX_CRACK_PROGRESS) {
                this.breakBlock(world, player, miningData.blockBreakId(), blockPos);
                return true;
            }
            sendBreakProgress(player, miningData.blockBreakId(), blockPos, progress);
            return false;
        });
    }

    private void breakBlock(DSWorld world, DSPlayer player, int blockBreakId, Point blockPos) {
        world.breakBlock(player, blockPos);
        sendResetBreakProgress(player, blockBreakId, blockPos);
    }

    @Override
    public void startBreaking(DSWorld world, DSPlayer player, Point blockPos, Block block) {
        final int time = this.getTicksToMine(world, blockPos, block, player);
        if (time < 0) {
            return;
        }
        this.addMiningData(player, blockPos);
    }

    @Override
    public void stopBreaking(DSPlayer player, Point blockPos) {
        final MiningData miningData = this.playerMiningData.get(player.getUuid());
        if (miningData == null) {
            return;
        }
        this.playerMiningData.remove(player.getUuid());
        sendResetBreakProgress(player, miningData.blockBreakId, blockPos);
    }

    private int getTicksToMine(DSWorld world, Point blockPos, Block block, DSPlayer player) {
        final BlockDefinition blockDefinition = this.blockFactory.getBlockDefinition(block);
        if (blockDefinition == null) {
            return UNBREAKABLE_TIME;
        }
        final MiningSpeedBehavior miningSpeedBehavior = blockDefinition.getBehavior(BlockBehavior.Type.MINING_SPEED);
        if (miningSpeedBehavior == null) {
            return UNBREAKABLE_TIME;
        }
        final int ticks = miningSpeedBehavior.getTicksToMine(world, blockPos, block, player);
        final ItemStack heldItem = player.getItemInMainHand();
        if (heldItem.material() == Material.AIR) {
            return ticks;
        }
        final ItemDefinition itemDefinition = world.itemFactory().getItemDefinition(heldItem);
        if (itemDefinition == null) {
            return ticks;
        }
        final net.desolatesky.item.behavior.MiningSpeedBehavior itemSpeedBehavior = itemDefinition.getBehavior(ItemBehavior.Type.MINING_SPEED);
        if (itemSpeedBehavior == null) {
            return ticks;
        }
        return itemSpeedBehavior.modifyTickSpeed(
                ticks,
                world,
                player,
                heldItem,
                blockPos,
                block
        );
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
