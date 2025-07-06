package net.desolatesky.cooldown;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class PlayerCooldowns {

    private final CooldownConfig cooldownConfig;
    private final Map<Key, CooldownData> cooldowns;

    public PlayerCooldowns(CooldownConfig cooldownConfig, Map<Key, CooldownData> cooldowns) {
        this.cooldownConfig = cooldownConfig;
        this.cooldowns = cooldowns;
    }

    public Duration getCooldownTime(Key key) {
        final CooldownData cooldown = this.cooldowns.get(key);
        if (cooldown == null) {
            return Duration.ZERO;
        }
        if (cooldown.isExpired()) {
            this.cooldowns.remove(key);
            return Duration.ZERO;
        }
        return cooldown.getTimeLeft();
    }

    public boolean addCooldown(Key key) {
        final CooldownData cooldownData = this.cooldownConfig.createCooldown(key);
        if (cooldownData == null) {
            return false;
        }
        this.cooldowns.put(key, cooldownData);
        return true;
    }

    public @Unmodifiable Collection<Key> getCooldownKeys() {
        return Collections.unmodifiableSet(this.cooldowns.keySet());
    }

    public @Nullable CooldownData getCooldownData(Key key) {
        return this.cooldowns.get(key);
    }

    public void addCooldown(Key key, Cooldown cooldown) {
        this.cooldowns.put(key, new CooldownData(cooldown));
    }

    public void removeCooldown(Key key) {
        this.cooldowns.remove(key);
    }

}
