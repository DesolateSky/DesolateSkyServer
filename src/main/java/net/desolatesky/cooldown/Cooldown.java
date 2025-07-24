package net.desolatesky.cooldown;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;

import java.time.Duration;

public sealed interface Cooldown extends Keyed permits Cooldown.Durational {

    static Cooldown empty(Key key) {
        return new Durational(key, Duration.ZERO);
    }

    static Cooldown create(Key key, Duration duration) {
        return new Durational(key, duration);
    }

    Duration duration();

    record Durational(Key key, Duration duration) implements Cooldown {

    }

}