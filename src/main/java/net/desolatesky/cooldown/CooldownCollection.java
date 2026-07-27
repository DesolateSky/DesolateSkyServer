package net.desolatesky.cooldown;

import net.desolatesky.lock.Lockable;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;

public final class CooldownCollection implements Lockable {

    private final Lockable holder;
    private final Map<Key, Cooldown> cooldowns;
    private final Map<Key, TickingCooldown> tickingCooldowns;

    public CooldownCollection(Lockable holder, Map<Key, Cooldown> cooldowns, Map<Key, TickingCooldown> tickingCooldowns) {
        this.holder = holder;
        this.cooldowns = cooldowns;
        this.tickingCooldowns = tickingCooldowns;
    }

    public void setCooldown(Key key, Cooldown cooldown) {
        this.lockWrite(() -> {
            this.cooldowns.put(key, cooldown);
            if (cooldown instanceof final TickingCooldown tickingCooldown) {
                this.tickingCooldowns.put(key, tickingCooldown);
            }
        });
    }

    public void setCooldown(Key key, Duration duration) {
        this.setCooldown(key, new DurationCooldown(Instant.now(), duration));
    }

    public boolean removeCooldown(Key key) {
        return this.lockWrite(() -> {
            this.tickingCooldowns.remove(key);
            return this.cooldowns.remove(key) != null;
        });
    }

    public boolean isOnCooldown(Key key) {
        return this.lockRead(() -> {
            final Cooldown cooldown = this.cooldowns.get(key);
            if (cooldown == null) {
                return false;
            }
            if (cooldown.isComplete()) {
                this.removeCooldown(key);
                return true;
            }
            return false;
        });
    }

    public double calculatePercentageCompleted(Key key) {
        return this.lockRead(() -> {
            final Cooldown cooldown = this.cooldowns.get(key);
            if (cooldown == null) {
                return 1.0;
            }
            return cooldown.calculatePercentageCompleted();
        });
    }

    public @Nullable Duration getTimeLeft(Key key) {
        return this.lockRead(() -> {
            final Cooldown cooldown = this.cooldowns.get(key);
            if (cooldown == null) {
                return null;
            }
            return cooldown.getTimeLeft();
        });
    }

    public @Nullable Duration getTimeLeft(CooldownTemplate template) {
        return this.getTimeLeft(template.key());
    }

    public void setCooldown(CooldownTemplate template) {
        this.setCooldown(template.key(), template.createFromNow());
    }

    public void addToCooldown(Key key, Duration duration) {
        this.lockWrite(() -> {
            final Cooldown cooldown = this.cooldowns.get(key);
            if (cooldown == null) {
                this.cooldowns.put(key, new DurationCooldown(Instant.now(), duration));
                return;
            }
            this.setCooldown(key, cooldown.add(duration));
        });
    }

    public void addToCooldown(Key key, Cooldown cooldown) {
        this.lockWrite(() -> {
            final Cooldown current = this.cooldowns.get(key);
            if (current == null) {
                this.cooldowns.put(key, cooldown);
                return;
            }
            this.setCooldown(key, current.add(cooldown.getTimeLeft()));
        });
    }

    public void addToCooldown(CooldownTemplate template) {
        this.addToCooldown(template.key(), template.createFromNow());
    }

    public void tick() {
        this.lockWrite(() -> {
            this.tickingCooldowns.entrySet().removeIf(e -> {
                final TickingCooldown cooldown = e.getValue();
                cooldown.tick();
                return cooldown.isComplete();
            });
        });
    }

    @Override
    public ReadWriteLock lock() {
        return this.holder.lock();
    }
}
