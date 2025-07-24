package net.desolatesky.teleport;

import net.desolatesky.instance.InstancePoint;
import net.desolatesky.player.DSPlayer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

public final class TeleportData implements Keyed {

    public static final double DISTANCE_THRESHOLD_SQUARED = Math.pow(0.2, 2);

    private final Key locationKey;
    private final int totalTicks;
    private final InstancePoint destination;
    private final DSPlayer player;
    private final boolean cancelOnMove;
    private final @Nullable Consumer<TeleportData> tickCallback;
    private final @Nullable Consumer<TeleportData> cancellationCallback;
    private final @Nullable Consumer<TeleportData> preSuccessCallback;
    private final @Nullable Consumer<TeleportData> postSuccessCallback;
    private InstancePoint previousLocation;
    private int ticksPassed;

    public TeleportData(
            Key key,
            int totalTicks,
            InstancePoint destination,
            DSPlayer player,
            boolean cancelOnMove,
            @Nullable Consumer<TeleportData> tickCallback,
            @Nullable Consumer<TeleportData> cancellationCallback,
            @Nullable Consumer<TeleportData> preSuccessCallback,
            @Nullable Consumer<TeleportData> postSuccessCallback
    ) {
        this.locationKey = key;
        this.totalTicks = totalTicks;
        this.destination = destination;
        this.player = player;
        this.cancelOnMove = cancelOnMove;
        this.previousLocation = player.getInstancePosition();
        this.tickCallback = tickCallback;
        this.cancellationCallback = cancellationCallback;
        this.preSuccessCallback = preSuccessCallback;
        this.postSuccessCallback = postSuccessCallback;
        this.ticksPassed = 0;
    }

    public TeleportData(Key key, int totalTicks, InstancePoint destination, DSPlayer player, boolean cancelOnMove) {
        this(key, totalTicks, destination, player, cancelOnMove, null, null, null, null);
    }

    public int totalTicks() {
        return this.totalTicks;
    }

    public InstancePoint destination() {
        return this.destination;
    }

    public DSPlayer player() {
        return this.player;
    }

    public boolean cancelOnMove() {
        return this.cancelOnMove;
    }

    public InstancePoint previousLocation() {
        return this.previousLocation;
    }

    public int ticksPassed() {
        return this.ticksPassed;
    }

    public void onTick() {
        if (this.tickCallback != null) {
            this.tickCallback.accept(this);
        }
    }

    public void onCancel() {
        if (this.cancellationCallback != null) {
            this.cancellationCallback.accept(this);
        }
    }

    public void onPreSuccess() {
        if (this.preSuccessCallback != null) {
            this.preSuccessCallback.accept(this);
        }
    }

    public void onPostSuccess() {
        if (this.postSuccessCallback != null) {
            this.postSuccessCallback.accept(this);
        }
    }

    public boolean checkMovement() {
        final InstancePoint currentLocation = this.player.getInstancePosition();
        if (this.previousLocation == null) {
            this.previousLocation = currentLocation;
            return true;
        }
        if (!Objects.equals(currentLocation.instance(), this.previousLocation.instance())) {
            return false;
        }
        if (this.cancelOnMove) {
            final double distance = this.previousLocation.pos().distanceSquared(currentLocation.pos());
            if (distance > DISTANCE_THRESHOLD_SQUARED) {
                return false;
            }
        }
        this.previousLocation = currentLocation;
        return true;
    }

    public boolean isComplete() {
        return this.ticksPassed >= this.totalTicks;
    }

    public int ticksRemaining() {
        return this.totalTicks - this.ticksPassed;
    }

    @Override
    public @NotNull Key key() {
        return this.locationKey;
    }

    public void incrementTicks() {
        this.ticksPassed++;
    }

    @Override
    public String toString() {
        return "TeleportData{" +
                "locationKey=" + this.locationKey +
                ", totalTicks=" + this.totalTicks +
                ", destination=" + this.destination +
                ", player=" + this.player +
                ", cancelOnMove=" + this.cancelOnMove +
                ", tickCallback=" + this.tickCallback +
                ", hasCancellationCallback=" + (this.cancellationCallback != null) +
                ", hasPreSuccessCallback=" + (this.preSuccessCallback != null) +
                ", hasPostSuccessCallback=" + (this.postSuccessCallback != null) +
                ", hasPreviousLocation=" + (this.previousLocation != null) +
                '}';
    }
}
