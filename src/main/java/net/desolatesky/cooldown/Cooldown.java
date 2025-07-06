package net.desolatesky.cooldown;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;

import java.time.Duration;

public interface Cooldown extends Keyed {

    static Cooldown empty(Key key) {
        return new CooldownImpl(key, Duration.ZERO);
    }

    static Cooldown create(Key key, Duration duration) {
        return new CooldownImpl(key, duration);
    }

    Duration duration();

    record CooldownImpl(Key key, Duration duration) implements Cooldown {

    }

}