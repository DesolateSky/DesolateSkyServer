package net.desolatesky.cooldown;

import java.time.Duration;

public interface Cooldown {

    boolean isComplete();

    Duration getTimeLeft();

}
