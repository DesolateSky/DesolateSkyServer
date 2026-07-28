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

    public DurationCooldown add(Duration duration) {
        return new DurationCooldown(this.start, this.duration.plus(duration));
    }

    @Override
    public boolean isComplete() {
        return this.getTimeLeft().isNegative();
    }

    @Override
    public Duration getTimeLeft() {
        return Duration.between(Instant.now(), this.start.plus(this.duration));
    }

    @Override
    public double calculatePercentageCompleted() {
        final double total = this.duration.toMillis();
        final double left = total - this.getTimeLeft().toMillis();
        if (left <= 0) {
            return 1;
        }
        return left / total;
    }
}
