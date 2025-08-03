package net.desolatesky.breaking;

import net.desolatesky.instance.DSInstance;
import net.desolatesky.player.DSPlayer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;

public final class BreakingData {

    private final DSInstance instance;
    private final DSPlayer player;
    private final BlockVec blockPos;
    private final Block block;
    private final int id;
    private Duration currentBreakingTime = Duration.ZERO;
    private Instant lastHitTime;
    private Instant lastSoundTime;
    private boolean currentlyBreaking = true;

    public BreakingData(DSInstance instance, DSPlayer player, BlockVec blockPos, Block block, int id, Instant lastHitTime) {
        this.instance = instance;
        this.player = player;
        this.blockPos = blockPos;
        this.block = block;
        this.id = id;
        this.lastHitTime = lastHitTime;
        this.lastSoundTime = Instant.MIN;
    }

    public BreakingData(DSInstance instance, DSPlayer player, BlockVec blockPos, Block block, int id) {
        this(instance, player, blockPos, block, id, Instant.now());
    }

    public DSInstance instance() {
        return this.instance;
    }

    public DSPlayer player() {
        return this.player;
    }

    public BlockVec blockPos() {
        return this.blockPos;
    }

    public Block block() {
        return this.block;
    }

    public int id() {
        return this.id;
    }

    public @Nullable Instant lastHitTime() {
        return this.lastHitTime;
    }

    public void setLastHitTime(Instant lastHitTime) {
        this.lastHitTime = lastHitTime;
    }

    public Instant lastSoundTime() {
        return this.lastSoundTime;
    }

    public void setLastSoundTime(Instant lastSoundTime) {
        this.lastSoundTime = lastSoundTime;
    }

    public boolean currentlyBreaking() {
        return this.currentlyBreaking;
    }

    public void setCurrentlyBreaking(boolean currentlyBreaking) {
        this.currentlyBreaking = currentlyBreaking;
        if (!currentlyBreaking) {
            this.lastHitTime = null;
        }
    }

    public Duration currentBreakingTime() {
        return this.currentBreakingTime;
    }

    public void hit() {
        if (this.lastHitTime == null) {
            this.lastHitTime = Instant.now();
            return;
        }
        final Duration duration = Duration.between(this.lastHitTime, Instant.now());
        this.currentBreakingTime = this.currentBreakingTime.plus(duration);
        this.lastHitTime = Instant.now();
    }

}
