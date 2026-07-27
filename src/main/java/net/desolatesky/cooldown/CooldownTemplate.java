package net.desolatesky.cooldown;

import net.desolatesky.util.Namespace;
import net.kyori.adventure.key.Key;

import java.time.Duration;
import java.time.Instant;

public interface CooldownTemplate {

    static CooldownTemplate createTicking(Key key, long ticks) {
        return new TickingTemplate(key, ticks);
    }

    static CooldownTemplate creatTicking(String key, long ticks) {
        return createTicking(Key.key(key), ticks);
    }

    static CooldownTemplate createDuration(Key key, Duration duration) {
        return new DurationTemplate(key, duration);
    }

    static CooldownTemplate createDuration(String key, Duration duration) {
        return createDuration(Namespace.key(key), duration);
    }

    Key key();

    Cooldown createNew(Instant start);

    default Cooldown createFromNow() {
        return this.createNew(Instant.now());
    }

    final class DurationTemplate implements CooldownTemplate {

        private final Key key;
        private final Duration duration;

        public DurationTemplate(Key key, Duration duration) {
            this.key = key;
            this.duration = duration;
        }

        @Override
        public Cooldown createNew(Instant start) {
            return new DurationCooldown(start, this.duration);
        }

        @Override
        public Key key() {
            return this.key;
        }
    }

    final class TickingTemplate implements CooldownTemplate {

        private final Key key;
        private final long ticks;

        public TickingTemplate(Key key, long ticks) {
            this.key = key;
            this.ticks = ticks;
        }

        @Override
        public Cooldown createNew(Instant start) {
            return new TickingCooldown(this.ticks);
        }

        @Override
        public Key key() {
            return this.key;
        }
    }

}
