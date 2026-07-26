package net.desolatesky.cooldown;

import java.time.Duration;
import java.time.Instant;

public final class DurationCooldown implements Cooldown {

    private final Instant start;
    private final Duration duration;

    public DurationCooldown(Instant start, Duration duration) {
        this.start = start;
        this.duration = duration;
    }

    @Override
    public boolean isComplete() {
        return this.getTimeLeft().isNegative();
    }

    @Override
    public Duration getTimeLeft() {
        return Duration.between(Instant.now(), this.start.plus(this.duration));
    }
}
