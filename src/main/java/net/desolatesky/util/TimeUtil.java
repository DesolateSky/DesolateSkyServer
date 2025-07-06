package net.desolatesky.util;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public final class TimeUtil {

    public static final int MILLIS_PER_TICK = 50;

    private TimeUtil() {
        throw new UnsupportedOperationException();
    }

    public static long toTicks(Duration duration) {
        return millisToTicks(duration.toMillis());
    }

    public static long toTicks(long time, TimeUnit timeUnit) {
        return millisToTicks(timeUnit.toMillis(time));
    }

    public static long ticksTo(long time, TimeUnit timeUnit) {
        return timeUnit.convert(ticksToMillis(time), TimeUnit.MILLISECONDS);
    }

    public static long ticksToMillis(long ticks) {
        return ticks * MILLIS_PER_TICK;
    }

    public static long millisToTicks(long ticks) {
        return ticks / MILLIS_PER_TICK;
    }

    public static Duration ticksToDuration(long ticks) {
        return Duration.ofMillis(ticksToMillis(ticks));
    }

}