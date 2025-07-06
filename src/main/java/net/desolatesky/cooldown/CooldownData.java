package net.desolatesky.cooldown;

import net.kyori.adventure.key.Key;

import java.time.Duration;
import java.time.Instant;

public record CooldownData(Cooldown cooldown, Instant start) {

    public static CooldownData complete(Key key)  {
        return new CooldownData(Cooldown.empty(key), Instant.now());
    }

    public CooldownData(Cooldown cooldown) {
        this(cooldown, Instant.now());
    }

    public CooldownData(Cooldown cooldown, Instant start) {
        this.cooldown = cooldown;
        this.start = start;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(this.start.plus(this.cooldown.duration()));
    }

    public Instant getEnd() {
        return this.start.plus(this.cooldown.duration());
    }

    public Duration getTimeLeft() {
        return Duration.between(Instant.now(), this.getEnd());
    }

}
