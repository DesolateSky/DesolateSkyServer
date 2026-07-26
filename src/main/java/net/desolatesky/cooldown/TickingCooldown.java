package net.desolatesky.cooldown;

import net.desolatesky.util.TimeUtil;

import java.time.Duration;

public final class TickingCooldown implements Cooldown {

    private int ticksLeft;

    public TickingCooldown(int ticksLeft) {
        this.ticksLeft = ticksLeft;
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
}
