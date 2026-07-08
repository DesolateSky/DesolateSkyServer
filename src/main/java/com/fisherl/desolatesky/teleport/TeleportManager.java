package com.fisherl.desolatesky.teleport;

import com.fisherl.desolatesky.lock.Lockable;
import com.fisherl.desolatesky.message.MessageHandler;
import com.fisherl.desolatesky.message.Messages;
import com.fisherl.desolatesky.player.DSPlayer;
import com.fisherl.desolatesky.util.ComponentUtil;
import com.fisherl.desolatesky.world.WorldManager;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class TeleportManager implements Lockable {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final MessageHandler messageHandler;
    private final WorldManager worldManager;
    private final Map<UUID, RequestData> requests;

    public TeleportManager(MessageHandler messageHandler, WorldManager worldManager) {
        this.messageHandler = messageHandler;
        this.worldManager = worldManager;
        this.requests = new HashMap<>();
    }


    public void teleport(DSPlayer player, TeleportLocation.Type type, UUID worldId, Point position) {
        this.teleport(player, new TeleportLocation(type, worldId, position));
    }

    public void teleport(DSPlayer player, TeleportLocation to) {
        this.lockWrite(() -> {
            final UUID id = player.getUuid();
            final RequestData previousData = this.requests.remove(id);
            if (previousData != null) {
                previousData.request().notifyTeleportCancelled(TeleportCancelReason.SUPERSEDED);
            }
            final Request request = new LocationRequest(this.messageHandler, player, to, Instant.now(), Duration.of(1, TimeUnit.SECONDS.toChronoUnit()));
            request.updateCountdown();
            this.requests.put(id, new RequestData(request));
        });
    }

    public void tick() {
        this.lockWrite(() -> this.requests.entrySet().removeIf(entry -> {
            final RequestData requestData = entry.getValue();
            requestData.tick();
            final Request request = requestData.request();
            if (request.isComplete()) {
                final TeleportLocation location = request.location();
                this.worldManager.loadWorld(location.worldId())
                        .whenComplete((world, exception) -> {
                            final DSPlayer player = request.teleporter();
                            if (!player.isOnline()) {
                                return;
                            }
                            if (exception != null || world == null) {
                                this.messageHandler.sendMessage(player, Messages.TELEPORT_CANCELLED);
                                return;
                            }
                            request.teleporter().setInstance((Instance) world, location.position());
                            request.notifyTeleportComplete();
                        });
                return true;
            }
            if (requestData.ticksPassed() % 20 != 0) {
                return false;
            }
            request.updateCountdown();
            return false;
        }));
    }

    @Override
    public ReadWriteLock lock() {
        return this.lock;
    }

    private static final class RequestData {

        private final Request request;
        private int ticksPassed;

        public RequestData(Request request) {
            this.request = request;
            this.ticksPassed = 0;
        }

        public Request request() {
            return this.request;
        }

        private void tick() {
            this.ticksPassed++;
        }

        public int ticksPassed() {
            return this.ticksPassed;
        }
    }

    private interface Request {

        DSPlayer teleporter();

        TeleportLocation location();

        Instant start();

        Duration duration();

        void updateCountdown();

        void notifyTeleportCancelled(TeleportCancelReason reason);

        void notifyTeleportComplete();

        Duration getTimeLeft();

        boolean isComplete();
    }

    private record LocationRequest(
            MessageHandler messageHandler,
            DSPlayer teleporter,
            TeleportLocation location,
            Instant start,
            Duration duration
    ) implements Request {

        @Override
        public void updateCountdown() {
            final long secondsLeft = this.getTimeLeft().toSeconds();
            if (secondsLeft <= 0) {
                return;
            }
            this.messageHandler.sendMessage(this.teleporter, Messages.TELEPORT_INTERVAL, Map.of("seconds-left", secondsLeft));
        }

        @Override
        public void notifyTeleportCancelled(TeleportCancelReason reason) {
            this.messageHandler.sendMessage(this.teleporter, Messages.TELEPORT_CANCELLED);
        }

        @Override
        public void notifyTeleportComplete() {
            final Point to = this.location().position();
            this.messageHandler.sendMessage(
                    this.teleporter,
                    Messages.TELEPORT_SUCCESS,
                    Map.of("x", to.blockX(),
                            "y", to.blockY(),
                            "z", to.blockZ()
                    )
            );
        }

        @Override
        public Duration getTimeLeft() {
            final Instant end = this.start.plus(this.duration);
            return Duration.between(Instant.now(), end);
        }

        @Override
        public boolean isComplete() {
            return this.getTimeLeft().isNegative();
        }
    }

    private record PlayerRequest(
            MessageHandler messageHandler,
            DSPlayer teleporter,
            TeleportLocation location,
            Instant start,
            Duration duration,
            DSPlayer to
    ) implements Request {

        // TODO
        @Override
        public void updateCountdown() {

        }

        // TODO
        @Override
        public void notifyTeleportCancelled(TeleportCancelReason reason) {

        }

        // TODO
        @Override
        public void notifyTeleportComplete() {

        }

        // TODO
        @Override
        public Duration getTimeLeft() {
            return Duration.ZERO;
        }

        // TODO
        @Override
        public boolean isComplete() {
            return false;
        }
    }

}
