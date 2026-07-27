package net.desolatesky.cooldown;

import java.time.Duration;

public final class Cooldowns {

    private Cooldowns() {
    }

    public static final CooldownTemplate ISLAND_CREATION = CooldownTemplate.createDuration("island_creation", Duration.ofMinutes(5));

}
