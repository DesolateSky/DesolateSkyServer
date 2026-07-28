package net.desolatesky.cooldown;

import net.desolatesky.util.TimeUtil;

import java.time.Duration;

public final class TickingCooldown implements Cooldown {

    private final long ticks;
    private long ticksLeft;

    public TickingCooldown(long ticks) {
        this.ticks = ticks;
        this.ticksLeft = ticks;
    }

    public void tick() {
        if (this.ticksLeft == 0) {
            return;
        }
        this.ticksLeft--;
    }

    @Override
    public boolean isComplete() {
        return this.ticksLeft == 0;
    }

    @Override
    public Duration getTimeLeft() {
        return Duration.ofMillis(TimeUtil.ticksToMillis(this.ticksLeft));
    }

    @Override
    public Cooldown add(Duration duration) {
        return new TickingCooldown(TimeUtil.durationToTicks(duration) + this.ticksLeft);
    }

    @Override
    public double calculatePercentageCompleted() {
        if (this.ticksLeft <= 0) {
            return 1;
        }
        return (double) (this.ticks - this.ticksLeft) / this.ticks;
    }
}
