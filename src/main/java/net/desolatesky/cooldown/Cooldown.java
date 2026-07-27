package net.desolatesky.cooldown;

import java.time.Duration;

public interface Cooldown {

    boolean isComplete();

    Duration getTimeLeft();

    Cooldown add(Duration duration);

    double calculatePercentageCompleted();

}
